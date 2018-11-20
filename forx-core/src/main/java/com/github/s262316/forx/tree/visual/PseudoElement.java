package com.github.s262316.forx.tree.visual;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.fracpete.romannumerals4j.RomanNumeralFormat;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.css.CSSPropertiesReference;
import com.github.s262316.forx.css.GeneratedContent;
import com.github.s262316.forx.css.NumberRep;
import com.github.s262316.forx.graphics.GraphicsContext;
import com.github.s262316.forx.newbox.AnonReason;
import com.github.s262316.forx.newbox.BlockBox;
import com.github.s262316.forx.newbox.Box;
import com.github.s262316.forx.newbox.PropertiesEndPoint;
import com.github.s262316.forx.newbox.Visual;
import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.FunctionValue;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.StringValue;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.ValueList;
import com.github.s262316.forx.style.selectors.PseudoElementType;
import com.github.s262316.forx.style.selectors.util.ValuesHelper;
import com.github.s262316.forx.tree.XAttribute;
import com.github.s262316.forx.tree.visual.util.XmlVNodes;
import com.google.common.collect.ImmutableMap;

public class PseudoElement implements Visual, VElement
{
    private XmlVElement subject;
    private PseudoElementType pseudoType;
    private Box visualPart;
    private Map<String, Integer> counters=new HashMap<>();
    private CSSPropertiesReference cssPropertiesReference;
    private PropertiesEndPoint postSplitInlineBox;
    
    private static final Map<String, Function<Integer, String>> LIST_STYLE_FORMATTERS=ImmutableMap.<String, Function<Integer, String>>builder()
            .put("decimal" , GeneratedContent::formatCounterAsDecimal)
            .put("decimal-leading-zero" , GeneratedContent::formatCounterAsDecimalLeadingZero)
            .put("lower-roman" , GeneratedContent::formatCounterAsLowerRoman)
            .put("upper-roman" , GeneratedContent::formatCounterAsUpperRoman)
            .put("lower-greek" , GeneratedContent::formatCounterAsLowerGreek)
            .put("lower-latin" , GeneratedContent::formatCounterAsLowerAlpha)
            .put("upper-latin" , GeneratedContent::formatCounterAsUpperAlpha)
            .put("armenian" , GeneratedContent::formatCounterAsArmenian)
            .put("georgian" , GeneratedContent::formatAsGeorgian)
            .put("lower-alpha" , GeneratedContent::formatCounterAsLowerAlpha)
            .put("upper-alpha" , GeneratedContent::formatCounterAsUpperAlpha)
            .build();

    public PseudoElement(XmlVElement subj, PseudoElementType pt, CSSPropertiesReference cssPropertiesReference)
    {
        this.subject=subj;
        this.pseudoType=pt;
        this.cssPropertiesReference=cssPropertiesReference;
    }

    public void setVisualBox(Box b)
    {
        visualPart=b;
    }    
    
    public Box getVisualBox()
    {
        return visualPart;
    }    
    
    @Override
    public VElement find_counter(String name)
    {
        VElement ve;

        if(counters.get(name) == null)
        {
            // the counter is not in our map... try the non-pseudo node
            ve=subject.find_counter(name);
        }
        else
            ve=this;

        return ve;
    }

    public VElement find_counter_before(String name)
    {
        return subject.find_counter(name);
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

    /* normal | none | [ <String> | <uri> | <counter> | attr(<identifier>) |
    open-quote | close-quote | no-open-quote | no-close-quote ]+ | inherit
     */
    public void create_generated_content(Value spec)
    {
        ValueList vl;
        String computed_content="";
        FunctionValue fv;

        if(spec.getClass().equals(ValueList.class))
        {
            vl=(ValueList) spec;
            for(Value v : vl.members)
            {
                if(v.getClass().equals(FunctionValue.class))
                {
                    fv=(FunctionValue) v;
                    if(fv.name.equals("url"))
                    {
                    }
                    else
                        computed_content+=extract_generated_content(v);
                }
                else
                    computed_content+=extract_generated_content(v);
            }
        }
        else
            computed_content=extract_generated_content(spec);

        subject.parse_and_add_text(computed_content, (InlineBox) visualPart);
    }

    @Override
    public InlineBox createAnonInlineBox(AnonReason anonReason)
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage(), cssPropertiesReference, anonReason);

        return BoxFactory.createAnonymousInlineFlowBox(anon);
    }

    @Override
    public BlockBox createAnonBlockBox(AnonReason anonReason)
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage(), cssPropertiesReference, anonReason);

        return BoxFactory.createAnonymousBlockFlowBox(anon);
    }

//    @Override
//    public Column createAnonColBox(AnonReason anonReason)
//    {
//        AnonVisual anon;
//
//        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage(), cssPropertiesReference, anonReason);
//
//        return BoxFactory.createAnonColBox(anon);
//    }
//
//    @Override
//    public TableBox createAnonTableBox(AnonReason anonReason)
//    {
//        AnonVisual anon;
//
//        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage(), cssPropertiesReference, anonReason);
//
//        return BoxFactory.createAnonTableBox(anon);
//    }
//
//    @Override
//    public InlineBlockRootBox createAnonInlineBlockRootBox(AnonReason anonReason)
//    {
//        AnonVisual anon;
//
//        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage(), cssPropertiesReference, anonReason);
//
//        return BoxFactory.createAnonInlineBlockRootBox(anon);
//    }
//
//    @Override
//    public TableRow createAnonRowBox(AnonReason anonReason)
//    {
//        AnonVisual anon;
//
//        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage(), cssPropertiesReference, anonReason);
//
//        return BoxFactory.createAnonRowBox(anon);
//    }
//
//    @Override
//    public CellBox createAnonCellBox(AnonReason anonReason)
//    {
//        AnonVisual anon;
//
//        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage(), cssPropertiesReference, anonReason);
//
//        return BoxFactory.createAnonCellBox(anon);
//    }

    private String extract_generated_content(Value v)
    {
        Identifier id;
        FunctionValue fv;
        StringValue sv;
        String computed_content="";

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier) v;
            if(id.ident.equals("normal") || id.ident.equals("none"))
            {
            } // will never happen
            else if(id.ident.equals("open-quote"))
                computed_content=open_quote();
            else if(id.ident.equals("close-quote"))
                computed_content=close_quote();
            else if(id.ident.equals("no-open-quote"))
                open_quote();
            else if(id.ident.equals("no-close-quote"))
                close_quote();
        }
        else if(v.getClass().equals(StringValue.class))
        {
            sv=(StringValue) v;
            computed_content=sv.str;
        }
        else if(v.getClass().equals(FunctionValue.class))
        {
            fv=(FunctionValue) v;
            if(fv.name.equals("url"))
            {
                // should never happen
                throw new IllegalStateException();
            }
            else if(fv.name.equals("counter"))
                computed_content=counter_function(fv.values);
            else if(fv.name.equals("counters"))
                computed_content=counters_function(fv.values);
            else if(fv.name.equals("attr"))
            {
                String attrName=null;
                Identifier ident;


                if(fv.values.members.size() == 0)
                    throw new IllegalStateException();

                v=fv.values.members.get(0);
                if(v.getClass().equals(Identifier.class))
                {
                    ident=(Identifier)v;
                    attrName=ident.ident;
                }

                XAttribute a=subject.getAttr(attrName);
                if(a != null)
                    computed_content=a.getValue();
            }
        }
        else
            throw new IllegalStateException();

        return computed_content;
    }

    // counters(name, String) or
    // counters(name, String, style)
    String counters_function(ValueList values)
    {
        String counterName= ValuesHelper.getIdentifier(values.members.get(0))
                .orElseThrow(CounterFunctionMalformedException::new);

        String separator=ValuesHelper.getString(values.members.get(1))
                .orElseThrow(CounterFunctionMalformedException::new);

        Function<Integer, String> listStyleFormatter=GeneratedContent::formatCounterAsDecimal;
        if(values.members.size()==3)
        {
            listStyleFormatter = ValuesHelper.getIdentifier(values.members.get(2))
                    .map(LIST_STYLE_FORMATTERS::get)
                    .orElse(GeneratedContent::formatCounterAsDecimal);
        }

        List<XmlVElement> path=XmlVNodes.pathToHere(subject);
        String generatedContent=path.stream()
                .map(v -> v.counter_value(counterName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(listStyleFormatter)
                .collect(Collectors.joining(separator));

        List<Optional<Integer>> aa=path.stream()
                .map(v -> v.counter_value(counterName))
                .collect(Collectors.toList());

        List<Optional<Integer>> bb=aa.stream()
                .filter(Optional::isPresent)
                .collect(Collectors.toList());

        List<Integer> cc=bb.stream()
                .map(Optional::get)
                .collect(Collectors.toList());

        List<String> dd=cc.stream()
                .map(listStyleFormatter)
                .collect(Collectors.toList());

        return generatedContent;
    }

    private String counter_function(ValueList values)
    {
        Value v;
        VElement counter_location;
        Identifier counter_name=null, counter_style;
        String result="";
        int value;
        RomanNumeralFormat romanFormat = new RomanNumeralFormat();

        v=values.members.get(0);
        if(v.getClass().equals(Identifier.class))
        {
            counter_name=(Identifier)v;

            counter_location=find_counter(counter_name.ident);
            if(counter_location == null)
            {
                reset_counter(counter_name.ident, 0);
                counter_location=find_counter(counter_name.ident);
            }
            value=counter_location.counter_value(counter_name.ident).get();

            if(values.members.size() > 1)
            {
                v=values.members.get(1);
                counter_style=(Identifier) v;

                if(counter_style.ident.equals("decimal"))
                {
                    result=String.valueOf(value);
                }
                else if(counter_style.ident.equals("decimal-leading-zero"))
                {
                    result=String.valueOf(value);
                    if(value < 10)
                        result="0" + result;
                }
                else if(StringUtils.equals(counter_style.ident, "lower-roman"))
                {
                    result=romanFormat.format(value).toLowerCase();
                }
                else if(StringUtils.equals(counter_style.ident, "upper-roman"))
                {
                    result=romanFormat.format(value);
                }
                //			else if(counter_style.ident=="lower-greek")
                //				;
                else if(counter_style.ident.equals("lower-latin"))
                    result=NumberRep.toAlphabeticalCount(value).toLowerCase();
                else if(counter_style.ident.equals("upper-latin"))
                    result=NumberRep.toAlphabeticalCount(value);
                //			else if(counter_style.ident=="armenian")
                //				;
                //			else if(counter_style.ident=="georgian")
                //				;
                else if(counter_style.ident.equals("lower-alpha"))
                    result=NumberRep.toAlphabeticalCount(value).toLowerCase();
                else if(counter_style.ident.equals("upper-alpha"))
                    result=NumberRep.toAlphabeticalCount(value);
                else
                {
                    result=String.valueOf(value);
                }
            }
            else
            {
                result=String.valueOf(value);
            }
        }

        return result;
    }

    @Override
    public Value getPropertyValue(String property, MediaType mediaType, PseudoElementType pseudoType)
    {
        return subject.getPropertyValue(property, mediaType, pseudoType);
    }

    @Override
    public Value getPropertyValue(String property, MediaType mediaType)
    {
        return getPropertyValue(property, mediaType, PseudoElementType.PE_BEFORE);
    }

    public void clearStyles()
    {
        subject.clearStyles();
    }

    public void setStyles(List<Declaration> decs)
    {
        subject.setStyles(decs);
    }

    private String open_quote()
    {
        Value v;
        ValueList vl;
        String quote="";
        StringValue sv;
        int i;

        v=subject.getPropertyValue("quotes", MediaType.MT_SCREEN, pseudoType);
        if(v.getClass().equals(ValueList.class))
        {
            vl=(ValueList)v;
            i=(subject.quote_level() * 2) % vl.members.size();
            sv=(StringValue) vl.members.get(i);
            quote=sv.str;
        }
        else
            throw new IllegalStateException();

        subject.inc_quote_level();

        return quote;
    }

    private String close_quote()
    {
        Value v;
        ValueList vl;
        String quote="";
        StringValue sv;
        int i;

        v=subject.getPropertyValue("quotes", MediaType.MT_SCREEN, pseudoType);
        if(v.getClass().equals(ValueList.class))
        {
            vl=(ValueList)v;

            subject.dec_quote_level();

            i=((subject.quote_level() * 2) + 1) % vl.members.size();
            sv=(StringValue) vl.members.get(i);
            quote=sv.str;
        }
        else
            throw new IllegalStateException();

        return quote;
    }

//    @Override
//    public Graphics2D get_canvas()
//    {
//        return subject.get_canvas();
//    }

    @Override
    public GraphicsContext getGraphicsContext()
    {
        return subject.getGraphicsContext();
    }

//    @Override
//    public void calculateBorders(PropertyAdaptor on, BorderDescriptor borderdesc)
//    {
//        BorderStylesImpl.resolveBorders(on, subject, borderdesc, pseudoType);
//    }
//
//    @Override
//    public void computeMarginProperties(PropertyAdaptor on, MarginDescriptor margindesc)
//    {
//        CSSPropertyComputer.computeMarginProperties(on, subject, margindesc, pseudoType);
//    }
//
//    @Override
//    public void workOutAbsolutePosition(PropertyAdaptor on, PositionDescriptor pd)
//    {
//        CSSPropertyComputer.workOutAbsolutePosition(on, subject, pd, pseudoType);
//    }
//
//    @Override
//    public void workOutFlowDimensions(PropertyAdaptor on, DimensionsDescriptor dd)
//    {
//        CSSPropertyComputer.workOutFlowDimensions(on, subject, dd, pseudoType);
//    }
//
//    @Override
//    public void workOutLineProperties(PropertyAdaptor on, LineDescriptor ld, GraphicsContext graphicsContext)
//    {
//        CSSPropertyComputer.workOutLineProperties(on, subject, ld, pseudoType, graphicsContext);
//    }
//
//    @Override
//    public void workOutTextProperties(PropertyAdaptor on, TextProperties tp)
//    {
//        CSSPropertyComputer.workOutTextProperties(on, subject, tp, pseudoType);
//    }
//
//    @Override
//    public void workOutWordProperties(PropertyAdaptor on, WordProperties wp)
//    {
//        CSSPropertyComputer.workOutWordProperties(on, subject, wp, pseudoType);
//    }
//
//    @Override
//    public Font workOutFontProperties(PropertyAdaptor on)
//    {
//        return CSSPropertyComputer.workOutFontProperties(on, subject, pseudoType);
//    }
//
//    @Override
//    public void workOutFloatProperties(PropertyAdaptor on, FloatProperties fp)
//    {
//        CSSPropertyComputer.workOutFloatProperties(on, subject, fp, pseudoType);
//    }
//
//    @Override
//    public void workoutColours(PropertyAdaptor on, ColourDescriptor coldesc)
//    {
//        CSSPropertyComputer.workoutColours(on, subject, coldesc, pseudoType);
//    }
//
//    @Override
//    public void workoutBlockProperties(PropertyAdaptor on, BlockProperties bp)
//    {
//        CSSPropertyComputer.workoutBlockProperties(on, this, bp, PseudoElementType.PE_NOT_PSEUDO);
//    }
//
//    @Override
//    public void workout_background_properties(PropertyAdaptor on, BackgroundProperties bp)
//    {
//        CSSPropertyComputer.workout_background_properties(on, this, bp, PseudoElementType.PE_NOT_PSEUDO);
//    }

    public String getDefaultStyleLanguage()
    {
        return subject.getDefaultStyleLanguage();
    }

    @Override
    public String resolve_resource(URL url)
    {
        return subject.resolve_resource(url);
    }

    @Override
    public void computed_value(String property, Value value)
    {
        subject.computed_value(property, value);
    }
    
	@Override
	public AnonReason getAnonReason()
	{
		return null;
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
