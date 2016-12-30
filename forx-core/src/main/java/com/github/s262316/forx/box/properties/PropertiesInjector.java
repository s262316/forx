package com.github.s262316.forx.box.properties;

import java.awt.Font;
import java.util.Arrays;

import com.github.s262316.forx.box.util.Border;
import com.github.s262316.forx.box.util.BorderEdge;
import com.github.s262316.forx.box.util.SpecialLength;
import com.github.s262316.forx.graphics.GraphicsContext;

public class PropertiesInjector
{
    // important to get this before any other properties
    public static void injectFontProperties(Visual visual, PropertyAdaptor on, HasFontProperties font)
    {
        Font f;    	
    	
        f=visual.workOutFontProperties(on);
        font.setFont(f);
    }
    
    public static void injectBorderProperties(Visual visual, PropertyAdaptor on, HasBorders borders)
    {
        BorderDescriptor borderdesc=new BorderDescriptor();
        visual.calculateBorders(on, borderdesc);

        Border bs[]=new Border[4];
        
        bs[0]=new Border();        
        bs[0].edge=BorderEdge.LEFT;
        bs[0].width=borderdesc.borderLeftWidth;
        bs[0].style=borderdesc.borderLeftStyle;
        bs[0].colour=borderdesc.borderLeftColour;

        bs[1]=new Border();        
        bs[1].edge=BorderEdge.RIGHT;
        bs[1].width=borderdesc.borderRightWidth;
        bs[1].style=borderdesc.borderRightStyle;
        bs[1].colour=borderdesc.borderRightColour;

        bs[2]=new Border();        
        bs[2].edge=BorderEdge.TOP;
        bs[2].width=borderdesc.borderTopWidth;
        bs[2].style=borderdesc.borderTopStyle;
        bs[2].colour=borderdesc.borderTopColour;

        bs[3]=new Border();        
        bs[3].edge=BorderEdge.BOTTOM;
        bs[3].width=borderdesc.borderBottomWidth;
        bs[3].style=borderdesc.borderBottomStyle;
        bs[3].colour=borderdesc.borderBottomColour;

        Arrays.sort(bs);

        for(int i=0; i < 4; i++)
        {
            switch(bs[i].edge)
            {
                case LEFT:
                    Border.BORDER_LEFT=i;
                    break;
                case RIGHT:
                    Border.BORDER_RIGHT=i;
                    break;
                case TOP:
                    Border.BORDER_TOP=i;
                    break;
                case BOTTOM:
                    Border.BORDER_BOTTOM=i;
                    break;
            }
        }

        borders.setBorders(bs);
        borders.setPaddings(borderdesc.paddingTopWidth, borderdesc.paddingBottomWidth, borderdesc.paddingLeftWidth, borderdesc.paddingRightWidth);
    }
    
	public static void injectTextProperties(Visual visual, PropertyAdaptor on, HasTextProperties text)
	{
        TextProperties tp=new TextProperties();
        visual.workOutTextProperties(on, tp);
        
        text.setTextIndent(tp.text_indent);
        text.setTextAlign(tp.text_align);
	}    

	public static void injectWordProperties(Visual visual, PropertyAdaptor on, HasWordProperties word)
	{
        WordProperties wp=new WordProperties();
        visual.workOutWordProperties(on, wp);

        word.setWordSpacing(wp.word_spacing);
        word.setLetterSpacing(wp.letter_spacing);
	}
    
	public static void injectLineProperties(Visual visual, PropertyAdaptor on, HasLineProperties line, GraphicsContext graphicsContext)
	{
        LineDescriptor ld=new LineDescriptor();
        visual.workOutLineProperties(on, ld, graphicsContext);

        line.setLineHeight((int) ld.lineHeight);
        line.setVerticalAlign(ld.verticalAlign);
	}	
	
	public static void injectColourProperties(Visual visual, PropertyAdaptor on, HasColour colour)
	{
        ColourDescriptor cd1=new ColourDescriptor();
        visual.workoutColours(on, cd1);
        
        colour.setForegroundColour(cd1.foreground);
	}
	
	public static void injectBlockProperties(Visual visual, PropertyAdaptor on, HasBlockProperties block)
	{
        BlockProperties bp=new BlockProperties();
        visual.workoutBlockProperties(on, bp);

        block.setClearance(bp.clear);
        block.setOverflow(bp.overflow);
	}
	
	public static void injectPositionedProperties(Visual visual, PropertyAdaptor on, HasAbsolutePosition abs)
	{
		PositionDescriptor pd=new PositionDescriptor();
        visual.workOutAbsolutePosition(on, pd);
        
        if(pd.left.equals(SpecialLength.SL_AUTO) && pd.right.equals(SpecialLength.SL_SPECIFIED))
            abs.setRelLeft(-pd.right.getInt());
        if(pd.right.equals(SpecialLength.SL_AUTO) && pd.left.equals(SpecialLength.SL_SPECIFIED))
            abs.setRelLeft(pd.left.getInt());

        if(pd.top.equals(SpecialLength.SL_AUTO) && pd.bottom.equals(SpecialLength.SL_SPECIFIED))
            abs.setRelTop(-pd.bottom.getInt());
        if(pd.bottom.equals(SpecialLength.SL_AUTO) && pd.top.equals(SpecialLength.SL_SPECIFIED))
            abs.setRelTop(pd.top.getInt());
	}	
	
	public static void injectFloatingProperties(Visual visual, PropertyAdaptor on, HasFloatProperties floating)
	{
		FloatProperties fp=new FloatProperties();
		visual.workOutFloatProperties(on, fp);

		floating.setFloatPosition(fp.flowPos);
	}	
	
	public static void inject(
			Visual visual,
			PropertyAdaptor on,
			HasAbsolutePosition abs,
			HasBackgroundProperties backg,
			HasBlockProperties block,
			HasBorders borders,
			HasColour colour,
			HasFloatProperties floating,
			HasFlowDimensions flow,
			HasFontProperties font,
			HasLineProperties line,
			HasMargins margins,
			HasTextProperties text,
			HasWordProperties word)
	{
        // important to get this before any other properties
		if(font!=null)
			injectFontProperties(visual, on, font);

		if(borders!=null)
			injectBorderProperties(visual, on, borders);

		if(text!=null)
			injectTextProperties(visual, on, text);

		if(word!=null)
			injectWordProperties(visual, on, word);
			
		if(line!=null)
			injectLineProperties(visual, on, line, visual.getGraphicsContext());
		
		if(colour!=null)
			injectColourProperties(visual, on, colour);
		
		if(block!=null)
			injectBlockProperties(visual, on, block);
		
		if(abs!=null)
			injectPositionedProperties(visual, on, abs);
		
		if(floating!=null)
			injectFloatingProperties(visual, on, floating);
	}


}


