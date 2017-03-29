package com.github.s262316.forx.box.properties;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.BoxError;
import com.github.s262316.forx.box.BoxExceptionType;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.util.BackgroundAttachment;
import com.github.s262316.forx.box.util.BackgroundRepeat;
import com.github.s262316.forx.box.util.Clearance;
import com.github.s262316.forx.box.util.FloatPosition;
import com.github.s262316.forx.box.util.FontStyle;
import com.github.s262316.forx.box.util.FontVariant;
import com.github.s262316.forx.box.util.ImagePlugin;
import com.github.s262316.forx.box.util.Overflow;
import com.github.s262316.forx.box.util.SpecialLength;
import com.github.s262316.forx.box.util.TextAlign;
import com.github.s262316.forx.box.util.VerticalAlignmentSpecial;
import com.github.s262316.forx.core.Prefs;
import com.github.s262316.forx.css.util.CSSColours;
import com.github.s262316.forx.graphics.FontUtils;
import com.github.s262316.forx.graphics.GraphicsContext;
import com.github.s262316.forx.tree.style.ColourValue;
import com.github.s262316.forx.tree.style.FunctionValue;
import com.github.s262316.forx.tree.style.HashValue;
import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.MediaType;
import com.github.s262316.forx.tree.style.NumericValue;
import com.github.s262316.forx.tree.style.StringValue;
import com.github.s262316.forx.tree.style.UrlValue;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.visual.VElement;


public class CSSPropertyComputer
{
	private final static Logger logger=LoggerFactory.getLogger(CSSPropertyComputer.class);

    public static void workoutBlockProperties(PropertyAdaptor on, VElement subj, BlockProperties bp, PseudoElementType pseudoType)
    {
        Value v;
        Identifier id;
        v=subj.getPropertyValue("clear", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier) v;
            if(id.ident.equals("none"))
                bp.clear=Clearance.C_NONE;
            else if(id.ident.equals("left"))
                bp.clear=Clearance.C_LEFT;
            else if(id.ident.equals("right"))
                bp.clear=Clearance.C_RIGHT;
            else if(id.ident.equals("both"))
                bp.clear=Clearance.C_BOTH;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        v=subj.getPropertyValue("overflow", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier) v;
            if(id.ident.equals("visible"))
                bp.overflow=Overflow.OF_VISIBLE;
            else if(id.ident.equals("hidden"))
                bp.overflow=Overflow.OF_HIDDEN;
            else if(id.ident.equals("scroll"))
                bp.overflow=Overflow.OF_SCROLL;
            else if(id.ident.equals("auto"))
                bp.overflow=Overflow.OF_SCROLL;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
    }
// margin-width : <length> | <percentage> | auto

    public static void computeMarginProperties(PropertyAdaptor on, VElement subj, MarginDescriptor margindesc, PseudoElementType pseudoType)
    {
        Value v;
        Identifier id;
        NumericValue nv;
        
// 'margin-top', 'margin-bottom' : <margin-width> | inherit
        v=subj.getPropertyValue("margin-top", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("auto"))
                margindesc.top.specified=SpecialLength.SL_AUTO;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
        	nv=(NumericValue)v;

            margindesc.top.set(nv.absLength(on));
        }        
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        subj.computed_value("margin-top", v);
        
        v=subj.getPropertyValue("margin-bottom", MediaType.MT_SCREEN, pseudoType);
        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("auto"))
                margindesc.bottom.specified=SpecialLength.SL_AUTO;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
        	nv=(NumericValue)v;

            margindesc.bottom.set(nv.absLength(on));
        }        
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        subj.computed_value("margin-bottom", v);
        
// 'margin-right', 'margin-left'  <margin-width> | inherit
        v=subj.getPropertyValue("margin-right", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("auto"))
                margindesc.right.specified=SpecialLength.SL_AUTO;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
        	nv=(NumericValue)v;

            margindesc.right.set(nv.absLength(on));
        }        
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        subj.computed_value("margin-right", v);
        
        v=subj.getPropertyValue("margin-left", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("auto"))
                margindesc.left.specified=SpecialLength.SL_AUTO;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
        	nv=(NumericValue)v;

            margindesc.left.set(nv.absLength(on));
        }                
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        subj.computed_value("margin-left", v);
    }
// float

    public static void workOutFloatProperties(PropertyAdaptor on, VElement subj, FloatProperties fp, PseudoElementType pseudoType)
    {
        Value v;
        Identifier id;
        v=subj.getPropertyValue("float", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("left"))
                fp.flowPos=FloatPosition.LEFT;
            else if(id.ident.equals("right"))
                fp.flowPos=FloatPosition.RIGHT;
            else
                fp.flowPos=FloatPosition.NONE;

            subj.computed_value("float", v);
        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
    }


    /*
    top : <length> | <percentage> | auto | inherit
    right :   <length> | <percentage> | auto | inherit
    bottom :   <length> | <percentage> | auto | inherit
    left :   <length> | <percentage> | auto | inherit
    z-index :   auto | <integer> | inherit
     */    
    public static void workOutAbsolutePosition(PropertyAdaptor on, VElement subj, PositionDescriptor pd, PseudoElementType pseudoType)
    {
        Value v;
        Identifier id;
        NumericValue nv;
        v=subj.getPropertyValue("z-index", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("auto"))
                pd.zIndex.specified=SpecialLength.SL_AUTO;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            pd.zIndex.set(nv.absLength(on));
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        v=subj.getPropertyValue("top", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("auto"))
                pd.top.specified=SpecialLength.SL_AUTO;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            pd.top.set(nv.absLength(on));
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        v=subj.getPropertyValue("right", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
			id=(Identifier)v;
            if(id.ident.equals("auto"))
                pd.right.specified=SpecialLength.SL_AUTO;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            pd.right.set(nv.absLength(on));
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        v=subj.getPropertyValue("bottom", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("auto"))
                pd.bottom.specified=SpecialLength.SL_AUTO;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            pd.bottom.set(nv.absLength(on));
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        v=subj.getPropertyValue("left", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
			id=(Identifier)v;
            if(id.ident.equals("auto"))
                pd.left.specified=SpecialLength.SL_AUTO;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            pd.left.set(nv.absLength(on));
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
    }
// width : <length> | <percentage> | auto | inherit
// height : <length> | <percentage> | auto | inherit
// min-width : <length> | <percentage> | inherit
// max-width : <length> | <percentage> | none | inherit
// min-height : <length> | <percentage> | inherit
// max-height :  <length> | <percentage> | none | inherit

    public static void workOutFlowDimensions(PropertyAdaptor on, VElement subj, DimensionsDescriptor dd, PseudoElementType pseudoType)
    {
        Value v;
        Identifier id;
        NumericValue nv;

        v=subj.getPropertyValue("width", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("auto"))
                dd.width.specified=SpecialLength.SL_AUTO;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
        	nv=(NumericValue)v;

            dd.width.set(nv.absLength(on));
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        subj.computed_value("width", v);
        v=subj.getPropertyValue("height", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("auto"))
                dd.height.specified=SpecialLength.SL_AUTO;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            dd.height.set(nv.absLength(on));
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        subj.computed_value("height", v);
        v=subj.getPropertyValue("min-width", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            dd.minWidth.set(nv.absLength(on));
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        subj.computed_value("min-width", v);
        v=subj.getPropertyValue("max-width", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("none"))
                dd.maxWidth.specified=SpecialLength.SL_NULL;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            dd.maxWidth.set(nv.absLength(on));
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        subj.computed_value("max-width", v);
        v=subj.getPropertyValue("min-height", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            dd.minHeight.set(nv.absLength(on));
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        subj.computed_value("min-height", v);
        v=subj.getPropertyValue("max-height", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("none"))
                dd.maxHeight.specified=SpecialLength.SL_NULL;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            dd.maxHeight.set(nv.absLength(on));
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        subj.computed_value("max-height", v);
    }
// line-height : normal | <number> | <length> | <percentage> | inherit
// vertical-align : baseline | sub | super | top | text-top | middle | bottom | text-bottom | <percentage> | <length> | inherit

    public static void workOutLineProperties(PropertyAdaptor on, VElement subj, LineDescriptor ld, PseudoElementType pseudoType, GraphicsContext graphicsContext)
    {
        Value v;
        Identifier id;
        InlineBox ilb;
        NumericValue nv;
// handle a number value and multiply out the line height.. if set to auto
// then return the font height in px
        v=subj.getPropertyValue("line-height", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("normal"))
            {
                ld.lineHeight=FontUtils.size_px(on.getFont(), graphicsContext.fontMetrics(on.getFont()));
                subj.computed_value("line-height", new Identifier("normal"));
            }
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            ld.lineHeight=nv.absLength(on);
            subj.computed_value("line-height", new NumericValue((int)ld.lineHeight, "px"));
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
// if(ilb=dynamic_cast<InlineBox*>(on))
// {
        v=subj.getPropertyValue("vertical-align", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("baseline"))
                ld.verticalAlign.specified=VerticalAlignmentSpecial.VAS_BASELINE;
            else if(id.ident.equals("sub"))
                ld.verticalAlign.specified=VerticalAlignmentSpecial.VAS_SUB;
            else if(id.ident.equals("super"))
                ld.verticalAlign.specified=VerticalAlignmentSpecial.VAS_SUPER;
            else if(id.ident.equals("top"))
                ld.verticalAlign.specified=VerticalAlignmentSpecial.VAS_TOP;
            else if(id.ident.equals("text-top"))
                ld.verticalAlign.specified=VerticalAlignmentSpecial.VAS_TEXT_TOP;
            else if(id.ident.equals("middle"))
                ld.verticalAlign.specified=VerticalAlignmentSpecial.VAS_MIDDLE;
            else if(id.ident.equals("bottom"))
                ld.verticalAlign.specified=VerticalAlignmentSpecial.VAS_BOTTOM;
            else if(id.ident.equals("text-bottom"))
                ld.verticalAlign.specified=VerticalAlignmentSpecial.VAS_TEXT_BOTTOM;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
            subj.computed_value("vertical-align", v);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            ld.verticalAlign.value=nv.absLength(on);
            ld.verticalAlign.specified=VerticalAlignmentSpecial.VAS_LENGTH;
            subj.computed_value("vertical-align", new NumericValue((int)ld.lineHeight, "px"));
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
// }
    }
// text-indent  <length> | <percentage> | inherit
// text-align   left | right | center | justify | inherit

    public static void workOutTextProperties(PropertyAdaptor on, VElement subj, TextProperties tp, PseudoElementType pseudoType)
    {
        Value v;
        NumericValue nv;
        Identifier id;
        v=subj.getPropertyValue("text-indent", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            tp.text_indent=nv.absLength(on);
        }
        else
            throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        subj.computed_value("text-indent", v);
        v=subj.getPropertyValue("text-align", MediaType.MT_SCREEN, pseudoType);

        if(v == null)
        {
            Value v2=subj.getPropertyValue("direction", MediaType.MT_SCREEN, pseudoType);
			System.out.println(v2);
            if(v2.equals(new Identifier("ltr")) == true)
                tp.text_align=TextAlign.TA_LEFT;
            else if(v2.equals(new Identifier("rtl")) == true)
                tp.text_align=TextAlign.TA_RIGHT;
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        else
        {

            if(v.getClass().equals(Identifier.class))
            {
                id=(Identifier)v;
                if(id.ident.equals("left"))
                    tp.text_align=TextAlign.TA_LEFT;
                else if(id.ident.equals("right"))
                    tp.text_align=TextAlign.TA_RIGHT;
                else if(id.ident.equals("center"))
                    tp.text_align=TextAlign.TA_CENTER;
                else if(id.ident.equals("justify"))
                    tp.text_align=TextAlign.TA_JUSTIFY;
                else
                    throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
            }
            else
                throw new BoxError(BoxExceptionType.BET_INCORRECT_PROPERTY_VALUE);
        }
        subj.computed_value("text-align", v);
    }
// letter-spacing
// word-spacing

    public static void workOutWordProperties(PropertyAdaptor on, VElement subj, WordProperties wp, PseudoElementType pseudoType)
    {
        Value v;
        Identifier id;
        NumericValue nv;
        
// normal | <length> | inherit
        v=subj.getPropertyValue("letter-spacing", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("normal"))
                wp.letter_spacing=0;
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }
        else if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            wp.letter_spacing=nv.absLength(on);
        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        subj.computed_value("letter-spacing", v);
// normal | <length> | inherit
        v=subj.getPropertyValue("word-spacing", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("normal"))
                wp.word_spacing=0;
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
            subj.computed_value("word-spacing", new NumericValue(0, "px"));
        }
        else if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            wp.word_spacing=nv.absLength(on);
            subj.computed_value("word-spacing", v);
        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
    }

    public static Font workOutFontProperties(PropertyAdaptor on, VElement subj, PseudoElementType pseudoType)
    {
		logger.debug("workOutFontProperties");

        Value v;
        Identifier id;
        StringValue sv;
        ValueList vl;
        NumericValue nv;
        List<String> familyName=new ArrayList<String>();
        FontStyle fontStyle;
        FontVariant fontVariant;
        int fontWeight;
        int fontSize=0;
        Font font;

        v=subj.getPropertyValue("font-family", MediaType.MT_SCREEN, pseudoType);
		logger.debug("font-family : "+v);

        if(v.getClass().equals(ValueList.class))
        {
			vl=(ValueList)v;
            for(Value it : vl.members)
            {
                if(it.getClass().equals(Identifier.class))
                {
                    id=(Identifier)it;
                    Prefs p=Prefs.getPrefs();
                    if(id.ident.equals("serif"))
                        familyName.add(p.serif());
                    else if(id.ident.equals("sans-serif"))
                        familyName.add(p.sans_serif());
                    else if(id.ident.equals("cursive"))
                        familyName.add(p.cursive());
                    else if(id.ident.equals("fantasy"))
                        familyName.add(p.fantasy());
                    else if(id.ident.equals("monospace"))
                        familyName.add(p.monospace());
                    else
                        familyName.add(id.ident);
                }
                else if(it.getClass().equals(StringValue.class))
                {
                    sv=(StringValue)it;

                    familyName.add(sv.str);
                }
                else
                    throw new BoxError(BoxExceptionType.BET_UNKNOWN);
            }
        }
        else if(v.getClass().equals(Identifier.class))
        {
			id=(Identifier)v;

            Prefs p=Prefs.getPrefs();
            if(id.ident.equals("serif"))
                familyName.add(p.serif());
            else if(id.ident.equals("sans-serif"))
                familyName.add(p.sans_serif());
            else if(id.ident.equals("cursive"))
                familyName.add(p.cursive());
            else if(id.ident.equals("fantasy"))
                familyName.add(p.fantasy());
            else if(id.ident.equals("monospace"))
                familyName.add(p.monospace());
            else
                familyName.add(id.ident);
        }
        else if(v.getClass().equals(StringValue.class))
        {
            sv=(StringValue)v;

            familyName.add(sv.str);
        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        subj.computed_value("font-family", v);
        v=subj.getPropertyValue("font-style", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("normal"))
                fontStyle=FontStyle.FS_NORMAL;
            else if(id.ident.equals("italic"))
                fontStyle=FontStyle.FS_ITALIC;
            else if(id.ident.equals("oblique"))
                fontStyle=FontStyle.FS_OBLIQUE;
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        subj.computed_value("font-style", v);
        v=subj.getPropertyValue("font-variant", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("normal"))
                fontVariant=FontVariant.FV_NORMAL;
            else if(id.ident.equals("small-caps"))
                fontVariant=FontVariant.FV_SMALLCAPS;
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        subj.computed_value("font-variant", v);
        v=subj.getPropertyValue("font-weight", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("normal"))
                fontWeight=400;
            else if(id.ident.equals("bold"))
                fontWeight=700;
            else if(id.ident.equals("bolder"))
            {
                fontWeight=FontUtils.weight(on.getContainer().getFont());
                fontWeight+=100;
                if(fontWeight>900)
                	fontWeight=900;
            }
            else if(id.ident.equals("lighter"))
            {
                fontWeight=FontUtils.weight(on.getContainer().getFont());
                fontWeight-=100;
                if(fontWeight<100)
                	fontWeight=100;
            }
            else if(id.ident.equals("100"))
                fontWeight=100;
            else if(id.ident.equals("200"))
                fontWeight=200;
            else if(id.ident.equals("300"))
                fontWeight=300;
            else if(id.ident.equals("400"))
                fontWeight=400;
            else if(id.ident.equals("500"))
                fontWeight=500;
            else if(id.ident.equals("600"))
                fontWeight=600;
            else if(id.ident.equals("700"))
                fontWeight=700;
            else if(id.ident.equals("800"))
                fontWeight=800;
            else if(id.ident.equals("900"))
                fontWeight=900;
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        subj.computed_value("font-weight", v);

        v=subj.getPropertyValue("font-size", MediaType.MT_SCREEN, pseudoType);
        if(v.getClass().equals(NumericValue.class))
        {
            nv=(NumericValue)v;

            int cont_font_size=FontUtils.size_pt(on.getContainer().getFont());
            fontSize=nv.absLength(on);
        }
        else if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("xx-small"))
                fontSize=8;
            else if(id.ident.equals("x-small"))
                fontSize=10;
            else if(id.ident.equals("small"))
                fontSize=12;
            else if(id.ident.equals("medium"))
                fontSize=12;
            else if(id.ident.equals("large"))
                fontSize=16;
            else if(id.ident.equals("x-large"))
                fontSize=18;
            else if(id.ident.equals("xx-large"))
                fontSize=22;
            else if(id.ident.equals("larger"))
            {
            	// get font size of container
                int cont_font_size=FontUtils.size_pt(on.getContainer().getFont());
                if(cont_font_size != 8 && cont_font_size != 22)
                    fontSize=cont_font_size + 2;
            }
            else if(id.ident.equals("smaller"))
            {
            	// get font size of container
                int cont_font_size=FontUtils.size_pt(on.getContainer().getFont());
                if(cont_font_size != 8 && cont_font_size != 22)
                    fontSize=cont_font_size - 2;
            }
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);

		logger.debug("putting font-size : "+fontSize);
		
        subj.computed_value("font-size", new NumericValue(fontSize, "pt"));

        font=FontUtils.get_font(familyName, fontStyle, fontVariant, fontWeight, fontSize, subj.getGraphicsContext());

        return font;
    }

    public static void workoutColours(PropertyAdaptor on, VElement subj, ColourDescriptor coldesc, PseudoElementType pseudoType)
    {
        ColourValue cv;
        Value v;
        Identifier id;
        HashValue hv;
        FunctionValue fv;
        UrlValue uv;
        v=subj.getPropertyValue("color", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;

            cv=CSSColours.normalise(id.ident);
            coldesc.foreground=new Color(cv.r, cv.g, cv.b);
        }
        else if(v.getClass().equals(HashValue.class))
        {
            hv=(HashValue)v;

            cv=CSSColours.normalise(hv.str());
            coldesc.foreground=new Color(cv.r, cv.g, cv.b);
        }
        else if(v.getClass().equals(FunctionValue.class))
        {
            fv=(FunctionValue)v;

            cv=CSSColours.normalise(fv);
            coldesc.foreground=new Color(cv.r, cv.g, cv.b);
        }
        else if(v.getClass().equals(ColourValue.class))
        {
            cv=(ColourValue)v;

            coldesc.foreground=new Color(cv.r, cv.g, cv.b);
        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        subj.computed_value("color", v);
    }

    public static void workout_background_properties(PropertyAdaptor on, VElement subj, BackgroundProperties bp, PseudoElementType pseudoType)
    {
		logger.debug("workout_background_properties");

        ColourValue cv;
        Value v;
        Identifier id;
        HashValue hv;
        FunctionValue fv;
        UrlValue uv;
        NumericValue nv;
        ValueList vl;
        v=subj.getPropertyValue("background-color", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;

			logger.debug("id : "+id);

            if(id.ident.equals("transparent"))
                bp.colour=null;
            else
            {
                cv=CSSColours.normalise(id.ident);
                bp.colour=new Color(cv.r, cv.g, cv.b);
            }
        }
        else if(v.getClass().equals(HashValue.class))
        {
            hv=(HashValue)v;

            cv=CSSColours.normalise(hv.str());
            bp.colour=new Color(cv.r, cv.g, cv.b);
        }
        else if(v.getClass().equals(FunctionValue.class))
        {
            fv=(FunctionValue)v;

            cv=CSSColours.normalise(fv);
            bp.colour=new Color(cv.r, cv.g, cv.b);
        }
        else if(v.getClass().equals(ColourValue.class))
        {
            cv=(ColourValue)v;

            bp.colour=new Color(cv.r, cv.g, cv.b);
        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        subj.computed_value("background-color", v);
        v=subj.getPropertyValue("background-image", MediaType.MT_SCREEN, pseudoType);

        if(v.getClass().equals(Identifier.class))
        {
            id=(Identifier)v;
            if(id.ident.equals("none"))
                bp.image=null;
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }
        else if(v.getClass().equals(UrlValue.class))
        {
            uv=(UrlValue)v;
            bp.image=new ImagePlugin(subj.resolve_resource(uv.url));

        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        if(bp.image != null)
        {
            v=subj.getPropertyValue("background-repeat", MediaType.MT_SCREEN, pseudoType);

            if(v.getClass().equals(Identifier.class))
            {
                id=(Identifier)v;
                if(id.ident.equals("repeat"))
                    bp.repeat=BackgroundRepeat.BR_REPEAT;
                else if(id.ident.equals("repeat-x"))
                    bp.repeat=BackgroundRepeat.BR_REPEAT_X;
                else if(id.ident.equals("repeat-y"))
                    bp.repeat=BackgroundRepeat.BR_REPEAT_Y;
                else if(id.ident.equals("no-repeat"))
                    bp.repeat=BackgroundRepeat.BR_NO_REPEAT;
                else
                    throw new BoxError(BoxExceptionType.BET_UNKNOWN);
            }
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
            v=subj.getPropertyValue("background-attachment", MediaType.MT_SCREEN, pseudoType);

            if(v.getClass().equals(Identifier.class))
            {
                if(id.ident.equals("scroll"))
                    bp.attachment=BackgroundAttachment.BA_SCROLL;
                else if(id.ident.equals("fixed"))
                    bp.attachment=BackgroundAttachment.BA_FIXED;
                else
                    throw new BoxError(BoxExceptionType.BET_UNKNOWN);
            }
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
            v=subj.getPropertyValue("background-position", MediaType.MT_SCREEN, pseudoType);

            if(v.getClass().equals(ValueList.class))
            {
                vl=(ValueList)v;
//   vector<Value*>::iterator it=vl.members.begin();

                if(vl.members.get(0).getClass().equals(Identifier.class))
                {
                    id=(Identifier)vl.members.get(0);
                    if(id.ident.equals("left"))
                        bp.horiz=0;
                    else if(id.ident.equals("center"))
                        bp.horiz=(on.contentWidth() / 2) - (bp.image.swidth().value / 2);
                    else if(id.ident.equals("right"))
                        bp.horiz=on.contentWidth() - bp.image.swidth().value + 1;
                    else
                        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
                }
                else if(vl.members.get(0).getClass().equals(NumericValue.class))
                {
                    nv=(NumericValue)vl.members.get(0);

                    bp.horiz=nv.absLength(on);
                }
                else
                    throw new BoxError(BoxExceptionType.BET_UNKNOWN);
                if(vl.size() == 2)
                {
                    if(vl.members.get(1).getClass().equals(Identifier.class))
                    {
                        id=(Identifier)vl.members.get(1);
                        if(id.ident.equals("top"))
                            bp.vert=0;
                        else if(id.ident.equals("center"))
                            bp.vert=(on.contentHeight() / 2) - (bp.image.sheight().value / 2);
                        else if(id.ident.equals("bottom"))
                            bp.vert=on.contentHeight() - bp.image.sheight().value + 1;
                        else
                            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
                    }
                    else if(vl.members.get(1).getClass().equals(NumericValue.class))
                    {
                        nv=(NumericValue)vl.members.get(1);

                        bp.vert=nv.absLength(on);
                    }
                    else
                        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
                }
                else
                    bp.vert=(on.contentHeight() / 2) - (bp.image.sheight().value / 2);
            }
            else
            {

                if(v.getClass().equals(Identifier.class))
                {
                    id=(Identifier)v;
                    if(id.ident.equals("left"))
                    {
                        bp.horiz=0;
                        bp.vert=(on.contentHeight() / 2) - (bp.image.sheight().value / 2);
                    }
                    else if(id.ident.equals("center"))
                    {
                        bp.horiz=(on.contentWidth() / 2) - (bp.image.swidth().value / 2);
                        bp.vert=(on.contentHeight() / 2) - (bp.image.sheight().value / 2);
                    }
                    else if(id.ident.equals("right"))
                    {
                        bp.horiz=on.contentWidth() - bp.image.swidth().value + 1;
                        bp.vert=(on.contentHeight() / 2) - (bp.image.sheight().value / 2);
                    }
                    else if(id.ident.equals("top"))
                    {
                        bp.horiz=(on.contentWidth() / 2) - (bp.image.swidth().value / 2);
                        bp.vert=0;
                    }
                    else if(id.ident.equals("center"))
                    {
                        bp.horiz=(on.contentWidth() / 2) - (bp.image.swidth().value / 2);
                        bp.vert=(on.contentHeight() / 2) - (bp.image.sheight().value / 2);
                    }
                    else if(id.ident.equals("bottom"))
                    {
                        bp.horiz=(on.contentWidth() / 2) - (bp.image.swidth().value / 2);
                        bp.vert=on.contentHeight() - bp.image.sheight().value + 1;
                    }
                    else
                        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
                }
                else if(v.getClass().equals(NumericValue.class))
                {
                    nv=(NumericValue)v;

                    bp.horiz=nv.absLength(on);
                    bp.vert=on.contentHeight() - bp.image.sheight().value + 1;
                }
                else
                    throw new BoxError(BoxExceptionType.BET_UNKNOWN);
            }
        }
    }
}
