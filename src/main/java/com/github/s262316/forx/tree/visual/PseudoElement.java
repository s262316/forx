package com.github.s262316.forx.tree.visual;

import java.awt.Font;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.BoxError;
import com.github.s262316.forx.box.BoxExceptionType;
import com.github.s262316.forx.box.CellBox;
import com.github.s262316.forx.box.Column;
import com.github.s262316.forx.box.InlineBlockRootBox;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.TableBox;
import com.github.s262316.forx.box.TableRow;
import com.github.s262316.forx.box.properties.BackgroundProperties;
import com.github.s262316.forx.box.properties.BlockProperties;
import com.github.s262316.forx.box.properties.BorderDescriptor;
import com.github.s262316.forx.box.properties.CSSPropertyComputer;
import com.github.s262316.forx.box.properties.ColourDescriptor;
import com.github.s262316.forx.box.properties.DimensionsDescriptor;
import com.github.s262316.forx.box.properties.FloatProperties;
import com.github.s262316.forx.box.properties.LineDescriptor;
import com.github.s262316.forx.box.properties.MarginDescriptor;
import com.github.s262316.forx.box.properties.PositionDescriptor;
import com.github.s262316.forx.box.properties.PropertyAdaptor;
import com.github.s262316.forx.box.properties.TextProperties;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.properties.WordProperties;
import com.github.s262316.forx.common.NumberRep;
import com.github.s262316.forx.css.BorderStyles;
import com.github.s262316.forx.graphics.GraphicsContext;
import com.github.s262316.forx.tree.XAttribute;
import com.github.s262316.forx.tree.style.Declaration;
import com.github.s262316.forx.tree.style.FunctionValue;
import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.MediaType;
import com.github.s262316.forx.tree.style.StringValue;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;

public class PseudoElement implements Visual, VElement
{
    private XmlVElement subject;
    private PseudoElementType pseudoType;
    private Box visualPart;
    private Map<String, Integer> counters;

    public PseudoElement(XmlVElement subj, PseudoElementType pt)
    {
        this.subject=subj;
        this.pseudoType=pt;
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
    public int counter_value(String name)
    {
        return counters.get(name);
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
    public InlineBox createAnonInlineBox()
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage());

        return BoxFactory.createAnonymousInlineFlowBox(anon);
    }

    @Override
    public BlockBox createAnonBlockBox()
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage());

        return BoxFactory.createAnonymousBlockFlowBox(anon);
    }

    @Override
    public Column createAnonColBox()
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage());

        return BoxFactory.createAnonColBox(anon);
    }

    @Override
    public TableBox createAnonTableBox()
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage());

        return BoxFactory.createAnonTableBox(anon);
    }

    @Override
    public InlineBlockRootBox createAnonInlineBlockRootBox()
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage());

        return BoxFactory.createAnonInlineBlockRootBox(anon);
    }

    @Override
    public TableRow createAnonRowBox()
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage());

        return BoxFactory.createAnonRowBox(anon);
    }

    @Override
    public CellBox createAnonCellBox()
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), subject.getDefaultStyleLanguage());

        return BoxFactory.createAnonCellBox(anon);
    }

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
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
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
                    throw new BoxError(BoxExceptionType.BET_UNKNOWN);

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
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);

        return computed_content;
    }

    // counters(name, String) or
    // counters(name, String, style)
    private String counters_function(ValueList values)
    {
        return "";
    }

    private String counter_function(ValueList values)
    {
        Value v;
        VElement counter_location;
        Identifier counter_name=null, counter_style;
        String result="";
        int value;

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
            value=counter_location.counter_value(counter_name.ident);

            if(values.members.size() > 1)
            {
                v=values.members.get(1);
                counter_style=(Identifier) v;

                //			if(counter_style.ident=="disc")
                //				;
                //			else if(counter_style.ident=="circle")
                //				;
                //			else if(counter_style.ident=="square")
                //				;
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
                //			else if(counter_style.ident=="lower-roman")
                //			{
                //			}
                //			else if(counter_style.ident=="upper-roman")
                //				;
                //			else if(counter_style.ident=="lower-greek")
                //				;
                else if(counter_style.ident.equals("lower-latin"))
                    result=NumberRep.translate_alpha_lower(value);
                else if(counter_style.ident.equals("upper-latin"))
                    result=NumberRep.translate_alpha_upper(value);
                //			else if(counter_style.ident=="armenian")
                //				;
                //			else if(counter_style.ident=="georgian")
                //				;
                else if(counter_style.ident.equals("lower-alpha"))
                    result=NumberRep.translate_alpha_lower(value);
                else if(counter_style.ident.equals("upper-alpha"))
                    result=NumberRep.translate_alpha_upper(value);
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
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);

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
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);

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

    @Override
    public void calculateBorders(PropertyAdaptor on, BorderDescriptor borderdesc)
    {
        BorderStyles.resolveBorders(on, subject, borderdesc, pseudoType);
    }

    @Override
    public void computeMarginProperties(PropertyAdaptor on, MarginDescriptor margindesc)
    {
        CSSPropertyComputer.computeMarginProperties(on, subject, margindesc, pseudoType);
    }

    @Override
    public void workOutAbsolutePosition(PropertyAdaptor on, PositionDescriptor pd)
    {
        CSSPropertyComputer.workOutAbsolutePosition(on, subject, pd, pseudoType);
    }

    @Override
    public void workOutFlowDimensions(PropertyAdaptor on, DimensionsDescriptor dd)
    {
        CSSPropertyComputer.workOutFlowDimensions(on, subject, dd, pseudoType);
    }

    @Override
    public void workOutLineProperties(PropertyAdaptor on, LineDescriptor ld, GraphicsContext graphicsContext)
    {
        CSSPropertyComputer.workOutLineProperties(on, subject, ld, pseudoType, graphicsContext);
    }

    @Override
    public void workOutTextProperties(PropertyAdaptor on, TextProperties tp)
    {
        CSSPropertyComputer.workOutTextProperties(on, subject, tp, pseudoType);
    }

    @Override
    public void workOutWordProperties(PropertyAdaptor on, WordProperties wp)
    {
        CSSPropertyComputer.workOutWordProperties(on, subject, wp, pseudoType);
    }

    @Override
    public Font workOutFontProperties(PropertyAdaptor on)
    {
        return CSSPropertyComputer.workOutFontProperties(on, subject, pseudoType);
    }

    @Override
    public void workOutFloatProperties(PropertyAdaptor on, FloatProperties fp)
    {
        CSSPropertyComputer.workOutFloatProperties(on, subject, fp, pseudoType);
    }

    @Override
    public void workoutColours(PropertyAdaptor on, ColourDescriptor coldesc)
    {
        CSSPropertyComputer.workoutColours(on, subject, coldesc, pseudoType);
    }

    @Override
    public void workoutBlockProperties(PropertyAdaptor on, BlockProperties bp)
    {
        CSSPropertyComputer.workoutBlockProperties(on, this, bp, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public void workout_background_properties(PropertyAdaptor on, BackgroundProperties bp)
    {
        CSSPropertyComputer.workout_background_properties(on, this, bp, PseudoElementType.PE_NOT_PSEUDO);
    }

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
}
