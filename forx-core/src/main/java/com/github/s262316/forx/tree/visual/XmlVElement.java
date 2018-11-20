package com.github.s262316.forx.tree.visual;

import java.awt.Font;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.css.CSSPropertiesReference;
import com.github.s262316.forx.css.PropertyReference;
import com.github.s262316.forx.css.StyleXNodes;
import com.github.s262316.forx.graphics.GraphicsContext;
import com.github.s262316.forx.newbox.AnonReason;
import com.github.s262316.forx.newbox.BlockBox;
import com.github.s262316.forx.newbox.Box;
import com.github.s262316.forx.newbox.InlineHeadless;
import com.github.s262316.forx.newbox.PropertiesEndPoint;
import com.github.s262316.forx.newbox.ReplaceableBoxPlugin;
import com.github.s262316.forx.newbox.SpaceFlag;
import com.github.s262316.forx.newbox.Visual;
import com.github.s262316.forx.newbox.adders.Adder;
import com.github.s262316.forx.newbox.adders.BlockBoxParentAdder;
import com.github.s262316.forx.newbox.adders.InlineBoxParentAdder;
import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.selectors.PseudoClassType;
import com.github.s262316.forx.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.NodeType;
import com.github.s262316.forx.tree.XNode;
import com.github.s262316.forx.tree.XNodes;
import com.github.s262316.forx.tree.XmlElement;
import com.github.s262316.forx.tree.XmlNode;
import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.tree.events2.XmlMouseEvent;
import com.github.s262316.forx.util.PseudoElements;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;


public class XmlVElement extends XmlElement implements Visual, VElement
{
	private final static Logger logger=LoggerFactory.getLogger(XmlVElement.class);
    private PropertiesEndPoint nodeBox;
    private GraphicsContext graphics_context;
    private int ql;
    private ReplaceableBoxPlugin replaced_plugin;
    private List<Declaration> localStyleDeclarations=new ArrayList<Declaration>();
    private Map<String, Value> computedValues=new HashMap<String, Value>();
	private Map<String, Integer> counters=new HashMap<String, Integer>();
	private CSSPropertiesReference cssPropertiesReference;
	// set after a box has been populated
	private Adder parentLocator;
    private PropertiesEndPoint postSplitInlineBox;
    
    public XmlVElement(String name, XmlVDocument doc, int id, GraphicsContext gfxCtx, EventDispatcher eventDispatcher, CSSPropertiesReference cssPropertiesReference)
    {
		super(name, doc, id, eventDispatcher);

		nodeBox=null;
		graphics_context=gfxCtx;
		ql=0;
		replaced_plugin=null;
		this.cssPropertiesReference=cssPropertiesReference;
	}

    public boolean isSensitiveTo(PseudoClassType classType)
    {
		return ((XmlVDocument)getDocument()).isSensitiveToPseudoClass(this, MediaType.MT_ALL, PseudoElementType.PE_NOT_PSEUDO, classType);
    }
    
    public boolean isSensitiveTo(PseudoElementType elementType)
    {
		return ((XmlVDocument)getDocument()).isSensitiveToPseudoElement(this, MediaType.MT_ALL, elementType, PseudoClassType.PCT_NO_CLASS);
    }
    
    @Override
    public Value getPropertyValue(String property, MediaType mediaType, PseudoElementType pseudoType)
    {
		logger.debug("getPropertyValue({},{},{})", property, mediaType, pseudoType);

		Declaration d;
		Value v=null;
		boolean found=false;

		// 1. cascade
		// 1.1. look in local element's declarations
		logger.debug("localStyleDeclarations.size() "+localStyleDeclarations.size());

		ListIterator<Declaration> rit=localStyleDeclarations.listIterator(localStyleDeclarations.size());
		while(!found && rit.hasPrevious())
		{
			d=rit.previous();
			if(d.getProperty().equals(property))
			{
				v=d.getValue();
				found=true;

				logger.debug("found in local element declarations");
			}
		}

		if(v==null)
		{
			logger.debug("looking in gloval stylesheet");

			// 1.2. look in global stylesheet
			v=((XmlVDocument)getDocument()).getPropertyValue(this, property, mediaType, pseudoType);
			if(v==null)
			{
				// 2. does it inherit?
				PropertyReference sd=cssPropertiesReference.getPropertyDescriptor(property);
				if(sd!=null)
				{
					// use inherited if not root node
					if(sd.inherited && visualParentNode()!=null)
					{
//						logger.debug("sd.inherited "+sd.inherited+" visualParentNode() null? "+(visualParentNode()==null));

						v=visualParentNode().computed_value(property);
						if(v==null)
						{
//							logger.debug("no inherited. getting default");

							v=sd.def;
						}
//						else
//							logger.debug("got inherited value "+v);

					}
					else
					{
//						logger.debug("does not inherit - getting default");

						// 2.2. get default
						v=sd.def;
					}
				}
				else
				{
					logger.debug("no such property " + property);
				}
			}
		}

		return v;
    }

    @Override
    public Value getPropertyValue(String property, MediaType mediaType)
    {
        return getPropertyValue(property, mediaType, PseudoElementType.PE_NOT_PSEUDO);
    }

    public void clearStyles()
    {
		localStyleDeclarations.clear();
    }

    public void setStyles(List<Declaration> decs)
    {
		localStyleDeclarations=decs;
    }

    public void self_pollenate()
    {
		// create a box for this element
		Identifier display, position;
		Value v;

		logger.debug("self_pollenate");

		if(visualParentNode().visualBox()!=null)
		{
			// populate the quote level
			XmlVElement prev=prev_velement();
			if(prev==null)
				prev=visualParentNode();
			if(prev!=null)
				ql=prev.quote_level();

			Counters.handleCounters(this);

			if(isSensitiveTo(PseudoClassType.PCT_HOVER))
			{
				// install a com.github.s262316.forx.css hover listener
				this.addListener(new CSSHoverHandler(this), XmlMouseEvent.class);
			}
			
			// create the visual box
			v=getPropertyValue("display", MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO);
			if(v.getClass().equals(Identifier.class))
			{
				display=(Identifier)v;
				if(!display.ident.equals("none"))
				{
					position=(Identifier)getPropertyValue("position", MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO);
					if(position.ident.equals("fixed"))
					{
						throw new NotImplementedException("fixed boxes");
					}
					else if(position.ident.equals("absolute"))
					{
						throw new NotImplementedException("absolute boxes");
						
//						nodeBox=BoxFactory.createAbsoluteBox(this);
//						parentLocator=new AbsoluteBoxParentAdder();
//						visualParentNode().getParentLocator().add(nodeBox);
					}
					else
					{
						Identifier floatp;

						floatp=(Identifier)getPropertyValue("float", MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO);
						if(floatp.ident.equals("none"))
						{
							// position must equal static or relative here

							if(display.ident.equals("inline"))
							{
								if(replaced_plugin==null)
								{
									nodeBox=BoxFactory.createInlineFlowBox(this);
									parentLocator=new InlineBoxParentAdder((InlineHeadless)nodeBox);
//									if(position.ident.equals("relative"))
//										nodeBox.set_relative(true);

									visualParentNode().getParentLocator().add((InlineHeadless)nodeBox);
								}
								else
								{
									throw new NotImplementedException("fixed boxes");
									
//									inlineMember=BoxFactory.createReplacedInlineFlowBox(this, replaced_plugin);
////									if(position.ident.equals("relative"))
////										inlineMember.set_relative(true);
//
//									visualParentNode().getParentLocator().add(nodeBox);
								}
							}
							else if(display.ident.equals("block"))
							{
								nodeBox=BoxFactory.createBlockFlowBox(this, replaced_plugin);
								parentLocator=new BlockBoxParentAdder((BlockBox)nodeBox);
//								if(position.ident.equals("relative"))
//									nodeBox.set_relative(true);

								visualParentNode().getParentLocator().add((BlockBox)nodeBox);
							}
							else if(display.ident.equals("list-item"))
							{
								throw new NotImplementedException("fixed boxes");
							}
							else if(display.ident.equals("run-in"))
							{
								throw new NotImplementedException("fixed boxes");
							}
							else if(display.ident.equals("inline-block"))
							{
								throw new NotImplementedException("fixed boxes");
								
//								if(replaced_plugin==null)
//								{
//									nodeBox=BoxFactory.createInlineBlockFlowBox(this);
//									parentLocator=new DefaultAdder(nodeBox);
////									if(position.ident.equals("relative"))
////										nodeBox.set_relative(true);
//									visualParentNode().getParentLocator().add(nodeBox);
//								}
//								else
//								{
//									inlineMember=BoxFactory.createReplacedInlineFlowBox(this, replaced_plugin);
//									parentLocator=new DefaultAdder(nodeBox);
////									if(position.ident.equals("relative"))
////										inlineMember.set_relative(true);
//									visualParentNode().getParentLocator().add(nodeBox);
//								}
							}
							else if(display.ident.equals("table"))
							{
								throw new NotImplementedException("table boxes");

//								nodeBox=BoxFactory.createTableBox(this);
//								parentLocator=new DefaultAdder(nodeBox);
////								if(position.ident.equals("relative"))
////									nodeBox.set_relative(true);
//								visualParentNode().getParentLocator().add(nodeBox);
							}
							else if(display.ident.equals("inline-table"))
							{
								throw new NotImplementedException("inline-table boxes");
							}
							else if(display.ident.equals("table-row-group"))
							{
								throw new NotImplementedException("table-row-group boxes");
								
//								tableMember=BoxFactory.createTableRowGroup(this);
//								parentLocator=new DefaultAdder(null); // TODO not null
//								visualParentNode().getParentLocator().add(nodeBox); // TODO not nodeBox
							}
							else if(display.ident.equals("table-header-group"))
							{
								throw new NotImplementedException("table-header-group boxes");
							}
							else if(display.ident.equals("table-footer-group"))
							{
								throw new NotImplementedException("table-footer-group boxes");
							}
							else if(display.ident.equals("table-row"))
							{
								throw new NotImplementedException("table-row boxes");
								
//								tableMember=BoxFactory.createTableRow(this);
//								parentLocator=new DefaultAdder(null); // TODO not null
//								visualParentNode().getParentLocator().add(nodeBox);
							}
							else if(display.ident.equals("table-column-group"))
							{
								throw new NotImplementedException("table-column-group boxes");
								
//								tableMember=BoxFactory.createTableColGroup(this);
//								parentLocator=new DefaultAdder(null); // TODO not null
//								visualParentNode().getParentLocator().add(nodeBox); // TODO not nodeBox
							}
							else if(display.ident.equals("table-column"))
							{
								throw new NotImplementedException("table-column boxes");
								
//								tableMember=BoxFactory.createTableColumn(this);
//								parentLocator=new DefaultAdder(null); // TODO not null
//								visualParentNode().getParentLocator().add(nodeBox); // TODO not nodeBox
							}
							else if(display.ident.equals("table-cell"))
							{
								throw new NotImplementedException("table-cell boxes");
								
//								nodeBox=BoxFactory.createTableCell(this, 1, 1);
//								tableMember=(TableMember)nodeBox;
//								if(position.ident.equals("relative"))
//									nodeBox.set_relative(true);
//								
//								parentLocator=new DefaultAdder(nodeBox);
//								visualParentNode().getParentLocator().add(nodeBox);
							}
							else if(display.ident.equals("table-caption"))
							{
								throw new NotImplementedException("table-caption boxes");
							}
							else
							{
								throw new NotImplementedException("fixed boxes");
							}
						}
						else
						{
							throw new NotImplementedException("float boxes");
							
//							nodeBox=BoxFactory.createFloatBox(this, replaced_plugin);
//							if(position.ident.equals("relative"))
//								nodeBox.set_relative(true);
//							parentLocator=new DefaultAdder(nodeBox);
//							visualParentNode().getParentLocator().add(nodeBox);
						}

						// the after pseudo element comes later
//						create_generated_content(PseudoElementType.PE_BEFORE);
					}
				}
			}
		}
		else
			logger.debug("visual parent box is null");
	}

    public void self_depollenate()
    {
    }

    @Override
	public void complete(boolean full)
	{
//		create_generated_content(PseudoElementType.PE_AFTER);
	}

    public void plant_root()
    {
		nodeBox=BoxFactory.createRootBox(this);
		parentLocator=new BlockBoxParentAdder((BlockBox)nodeBox);
    }

    public void unplant_root()
    {

    }

    public PropertiesEndPoint visualBox()
    {
        return nodeBox;
    }

    public XmlVElement visualParentNode()
    {
		if(parentNode()!=null && XmlVElement.class.isAssignableFrom(parentNode().getClass()))
		{
			return (XmlVElement)parentNode();
		}
		else
		{
			return null;
		}
    }

    @Override
    public GraphicsContext getGraphicsContext()
    {
        return graphics_context;
    }

    @Override
    public InlineHeadless createAnonInlineBox(AnonReason anonReason)
    {
		AnonVisual anon;

		anon=new AnonVisual(this, getGraphicsContext(), getDefaultStyleLanguage(), cssPropertiesReference, anonReason);

		return BoxFactory.createAnonymousInlineFlowBox(anon);
    }

    @Override
    public BlockBox createAnonBlockBox(AnonReason anonReason)
    {
		AnonVisual anon;

		anon=new AnonVisual(this, getGraphicsContext(), getDefaultStyleLanguage(), cssPropertiesReference, anonReason);

		return BoxFactory.createAnonymousBlockFlowBox(anon);
    }

    public void parse_and_add_text(String value, PropertiesEndPoint container_box)
    {
		logger.debug("parse_and_add_text {, {}}", value, container_box);

		List<String> wordsAsList=Splitter.on(CharMatcher.whitespace()).omitEmptyStrings().splitToList(value);
		String words[]=Iterables.toArray(wordsAsList, String.class);
		
		PseudoElement firstLinePseudo=null, firstLetterPseudo=null;
		
		if(words.length!=0)
		{
		//	if(Boxes.isFirstForContent(normalParentBox) &&
		//			isSensitiveTo(PseudoElementType.PE_FIRST_LINE))
		//	{
		//		firstLinePseudo=createFirstLinePsuedo();
		//	}
	
			XmlVElement d=PseudoElements.nearestParentWithFirstLetter(this);
			List<XmlNode> path=XNodes.parentToChildPath(d, this);
			
//			if(d!=null && StyleXNodes.isFirstForContent(path))
//			{
//				firstLetterPseudo=createFirstLetterPsuedo(d);
//
//				Pair<String, String> firstWordSplit=PseudoElements.firstLetter(words[0]);
//
//				firstLetterPseudo.getVisualBox().flow_back(firstWordSplit.getLeft(), SpaceFlag.NOT_SPACE);
//				
//				if(firstWordSplit.getRight().equals(""))
//					words=ArrayUtils.remove(words, 0);
//				else
//					words[0]=firstWordSplit.getRight();
//			}
			
			for(int i=0; i<words.length-1; i++)
			{
				parentLocator.add(words[i], SpaceFlag.NOT_SPACE);
				parentLocator.add(" ", SpaceFlag.SPACE);
			}
	
			if(words.length>0)
				parentLocator.add(words[words.length-1], SpaceFlag.NOT_SPACE);
		}
    }

    public XmlVElement prev_velement()
    {
		XNode xn;
		boolean found=false;

		xn=this;
		do
		{
			xn=xn.previousNode();
			if(xn!=null)
				found=(xn.type()==NodeType.X_ELEMENT);
		}
		while(xn!=null && !found);

		return (XmlVElement)xn;
    }

    public int quote_level()
    {
		return ql;
    }

    public void inc_quote_level()
    {
		ql++;
    }

    public void dec_quote_level()
    {
		ql--;
    }

    public void set_plugin_handler(ReplaceableBoxPlugin repl)
    {
		replaced_plugin=repl;
    }

//    @Override
//    public void calculateBorders(PropertyAdaptor on, BorderDescriptor borderdesc)
//    {
//		BorderStylesImpl.resolveBorders(on, this, borderdesc, PseudoElementType.PE_NOT_PSEUDO);
//    }
//
//    @Override
//    public void computeMarginProperties(PropertyAdaptor on, MarginDescriptor margindesc)
//    {
//		CSSPropertyComputer.computeMarginProperties(on, this, margindesc, PseudoElementType.PE_NOT_PSEUDO);
//    }
//
//    @Override
//    public void workOutAbsolutePosition(PropertyAdaptor on, PositionDescriptor pd)
//    {
//		CSSPropertyComputer.workOutAbsolutePosition(on, this, pd, PseudoElementType.PE_NOT_PSEUDO);
//    }
//
//    @Override
//    public void workOutFlowDimensions(PropertyAdaptor on, DimensionsDescriptor dd)
//    {
//		CSSPropertyComputer.workOutFlowDimensions(on, this, dd, PseudoElementType.PE_NOT_PSEUDO);
//    }
//
//    @Override
//    public void workOutLineProperties(PropertyAdaptor on, LineDescriptor ld, GraphicsContext graphicsContext)
//    {
//		CSSPropertyComputer.workOutLineProperties(on, this, ld, PseudoElementType.PE_NOT_PSEUDO, graphicsContext);
//    }
//
//    @Override
//    public void workOutTextProperties(PropertyAdaptor on, TextProperties tp)
//    {
//		CSSPropertyComputer.workOutTextProperties(on, this, tp, PseudoElementType.PE_NOT_PSEUDO);
//    }
//
//    @Override
//    public void workOutWordProperties(PropertyAdaptor on, WordProperties wp)
//    {
//		CSSPropertyComputer.workOutWordProperties(on, this, wp, PseudoElementType.PE_NOT_PSEUDO);
//    }
//
//    @Override
//    public Font workOutFontProperties(PropertyAdaptor on)
//    {
//		return CSSPropertyComputer.workOutFontProperties(on, this, PseudoElementType.PE_NOT_PSEUDO);
//    }
//
//    @Override
//    public void workOutFloatProperties(PropertyAdaptor on, FloatProperties fp)
//    {
//		CSSPropertyComputer.workOutFloatProperties(on, this, fp, PseudoElementType.PE_NOT_PSEUDO);
//    }
//
//    @Override
//    public void workoutColours(PropertyAdaptor on, ColourDescriptor coldesc)
//    {
//		CSSPropertyComputer.workoutColours(on, this, coldesc, PseudoElementType.PE_NOT_PSEUDO);
//    }
//
//    @Override
//    public void workoutBlockProperties(PropertyAdaptor on, BlockProperties bp)
//    {
//		CSSPropertyComputer.workoutBlockProperties(on, this, bp, PseudoElementType.PE_NOT_PSEUDO);
//    }
//
//    @Override
//    public void workout_background_properties(PropertyAdaptor on, BackgroundProperties bp)
//    {
//		CSSPropertyComputer.workout_background_properties(on, this, bp, PseudoElementType.PE_NOT_PSEUDO);
//    }

    @Override
    public VElement find_counter(String name)
    {
        VElement ve;

        if(!counters.containsKey(name))
        {
        	if(visualParentNode()!=null)
			{
				// the counter is not in our map... try the parent node
				ve = visualParentNode().find_counter(name);
			}
			else
				return null;
        }
        else
            ve=this;

        return ve;
    }

    public VElement find_counter_before(String name)
    {
        return visualParentNode().find_counter(name);
    }

    @Override
    public void inc_counter(String name, int amount)
    {
        int count=counters.get(name);
        count+=amount;
        counters.put(name, count);
    }

    @Override
    public void reset_counter(String name, int to)
    {
        counters.put(name, to);
    }

    @Override
    public Optional<Integer> counter_value(String name)
    {
        return Optional.ofNullable(counters.get(name));
    }

//	private void create_generated_content(PseudoElementType pseudoType)
//	{
//		Value v;
//		Identifier ident;
//
//		v=getPropertyValue("content", MediaType.MT_SCREEN, pseudoType);
//		if(v.getClass().equals(Identifier.class))
//		{
//			ident=(Identifier)v;
//			if(ident.ident.equals("normal") || ident.ident.equals("none"))
//			{
//				// no pseudo element here.. let's go!
//			}
//			else
//				create_generated_content(pseudoType, v);
//		}
//		else
//			create_generated_content(pseudoType, v);
//	}

//	private void create_generated_content(PseudoElementType pseudoType, Value spec)
//	{
//		Identifier display;
//		Box pseudoBox=null;
//		PseudoElement pe;
//		Value v;
//
//		v=getPropertyValue("display", MediaType.MT_SCREEN, pseudoType);
//		if(v.getClass().equals(Identifier.class))
//		{
//			display=(Identifier)v;
//
//			pe=new PseudoElement(this, PseudoElementType.PE_BEFORE, cssPropertiesReference);
//
//			if(display.ident.equals("inline"))
//				pseudoBox=BoxFactory.createInlineFlowBox(pe);
//			else if(display.ident.equals("block"))
//				pseudoBox=BoxFactory.createBlockFlowBox(pe, null);
//
//			pe.setVisualBox(pseudoBox);
//
//			Counters.handleCounters(pe);
//
//			BoxTypes.toBox(nodeBox).flow_back(pseudoBox);
//
//			pe.create_generated_content(spec);
//		}
//		else
//			throw new BoxError(BoxExceptionType.BET_UNKNOWN);
//	}
//
//	private PseudoElement createFirstLinePsuedo()
//	{
//		PseudoElement pe;
//		Box pseudoBox;
//
//		pe=new PseudoElement(this, PseudoElementType.PE_FIRST_LINE, cssPropertiesReference);
//		pseudoBox=BoxFactory.createInlineFlowBox(pe);
//		pe.setVisualBox(pseudoBox);
//		
//		BoxTypes.toBox(nodeBox).flow_back(pseudoBox);
//		
//		return pe;
//	}
//
//	private PseudoElement createFirstLetterPsuedo(XmlVElement psuedoLetterDeclared)
//	{
//		PseudoElement pe;
//		Box pseudoBox;
//
//		pe=new PseudoElement(psuedoLetterDeclared, PseudoElementType.PE_FIRST_LETTER, cssPropertiesReference);
//		pseudoBox=BoxFactory.createInlineFlowBox(pe);
//		pe.setVisualBox(pseudoBox);
//		
//		BoxTypes.toBox(nodeBox).flow_back(pseudoBox);
//		
//		return pe;
//	}
	
	public String getDefaultStyleLanguage()
	{
		return ((XmlVDocument)getDocument()).getDefaultStyleLanguage();
	}

    @Override
    public String resolve_resource(URL url)
    {
		return "";
	}

    @Override
    public void computed_value(String property, Value value)
    {
		logger.debug("computed_value( : "+property+", "+value);

		computedValues.put(property, value);
	}

    public Value computed_value(String property)
    {
		return computedValues.get(property);
	}

	@Override
	public AnonReason getAnonReason()
	{
		return null;
	}

	private Adder getParentLocator()
	{
		return parentLocator;
	}
	
	@Override
	public void setPostSplit(PropertiesEndPoint postSplitInlineBox)
	{
		this.postSplitInlineBox=postSplitInlineBox;
	}

	@Override
	public PropertiesEndPoint getPostSplit()
	{
		return postSplitInlineBox;
	}
}
