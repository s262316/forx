package com.github.s262316.forx.box;

import com.github.s262316.forx.box.properties.DimensionsDescriptor;
import com.github.s262316.forx.box.properties.HasFloatProperties;
import com.github.s262316.forx.box.properties.MarginDescriptor;
import com.github.s262316.forx.box.properties.PropertiesInjector;
import com.github.s262316.forx.box.properties.PropertyBoxAdaptor;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.util.FloatPosition;
import com.github.s262316.forx.box.util.Length;
import com.github.s262316.forx.box.util.SizeResult;
import com.github.s262316.forx.box.util.SpecialLength;

public class FloatBox extends BlockBox implements HasFloatProperties
{
    private FloatPosition floatPosition;
    private boolean _stretchy_cwidth;
    private boolean floated;
    private FlowContext flowContext=new FlowContext();

    public FloatBox(Visual visual, ReplaceableBoxPlugin p)
    {
        super(visual, null, p);
        floated=false;
    }
    
    @Override
    public void computeProperties()
    {
		PropertiesInjector.inject(visual, new PropertyBoxAdaptor(this), this, this, this, this, this, this, this, this, this, this, this, this);
    }

//    @Override
//    public void compute_properties()
//    {
//        FloatProperties fp=new FloatProperties();
//        Font f;
//        BorderDescriptor bd=new BorderDescriptor();
//        TextProperties tp=new TextProperties();
//        WordProperties wp=new WordProperties();
//        LineDescriptor ld=new LineDescriptor();
//        ColourDescriptor cd=new ColourDescriptor();
//        PositionDescriptor pd=new PositionDescriptor();
//        BlockProperties bp=new BlockProperties();
//        int space_width=0, space_height=0;
//
//        // important to get this before any other properties
//        f=visual.workOutFontProperties(this);
//        set_font(f);
//
//        visual.calculateBorders(this, bd);
//        visual.workOutTextProperties(this, tp);
//        visual.workOutWordProperties(this, wp);
//        visual.workOutLineProperties(this, ld);
//        visual.workOutFloatProperties(this, fp);
//        visual.workoutColours(this, cd);
//        visual.workoutBlockProperties(this, bp);
//
//        if(is_relative() == true)
//        {
//            visual.workOutAbsolutePosition(this, pd);
//            if(pd.left.specified == SpecialLength.SL_AUTO && pd.right.specified == SpecialLength.SL_SPECIFIED)
//                set_rel_left(-pd.right.value);
//            if(pd.right.specified == SpecialLength.SL_AUTO && pd.left.specified == SpecialLength.SL_SPECIFIED)
//                set_rel_left(pd.left.value);
//
//            if(pd.top.specified == SpecialLength.SL_AUTO && pd.bottom.specified == SpecialLength.SL_SPECIFIED)
//                set_rel_top(-pd.bottom.value);
//            if(pd.bottom.specified == SpecialLength.SL_AUTO && pd.top.specified == SpecialLength.SL_SPECIFIED)
//                set_rel_top(pd.top.value);
//        }
//
//        set_borders(bd);
//
//        _text_indent=tp.text_indent;
//        _text_align=tp.text_align;
//
//        _line_height=(int)ld.lineHeight;
//        // no vertical-align for block boxes
//
//        _letter_spacing=wp.letter_spacing;
////	if(wp.word_spacing==SpecialLength.SL_AUTO)
////		_word_spacing=0;
////	else
//        _word_spacing=wp.word_spacing;
//
//        position=fp.flowPos;
//
//        _foreground_colour=cd.foreground;
//
//        _clearance=bp.clear;
//    }

    @Override
    public SizeResult compute_dimensions()
    {
        DimensionsDescriptor dd=new DimensionsDescriptor();
        MarginDescriptor md=new MarginDescriptor();
        SizeResult result=new SizeResult();

        visual.workOutFlowDimensions(new PropertyBoxAdaptor(this), dd);
        visual.computeMarginProperties(new PropertyBoxAdaptor(this), md);

        if(dd.width.specified == SpecialLength.SL_AUTO)
            _stretchy_cwidth=true;
        else
            _stretchy_cwidth=false;

        calculateWidth(dd.width, md.left, md.right, dd.height);
        calculateHeight(dd.height, md.top, md.bottom, dd.width);

        // not sure about this, but if the width is the same as last time, then use
        // the height from last time too. this is because we're infinite looping when
        // an auto height is reset to 0 and the relayout is recalled which resizes
        // and relayouts, etc... might need to take margins into account
        if(dd.width.value == contentWidth())
            result.height.set(contentHeight());
        else
            result.height=dd.height;

        result.width=dd.width;
        result.marginLeft=md.left;
        result.marginRight=md.right;
//	result.height=dd.height;
        result.marginTop=md.top;
        result.marginBottom=md.bottom;

        return result;
    }

    public void calculateWidth(Length width, Length marginLeft, Length marginRight, Length height)
    {
        if(replace_object() != null)
        {
            if(marginLeft.specified == SpecialLength.SL_AUTO)
                marginLeft.set(0);
            if(marginRight.specified == SpecialLength.SL_AUTO)
                marginRight.set(0);

            if(width.specified == SpecialLength.SL_SPECIFIED && height.specified == SpecialLength.SL_SPECIFIED)
            {
                // do nothing
//			size.width=width;
//			size.height=height;
            }
            else if(width.specified == SpecialLength.SL_SPECIFIED && height.specified == SpecialLength.SL_AUTO)
            {
//			size.width=width;

                if(replace_object().ratio() < 0)
                {
                    if(replace_object().sheight().specified == SpecialLength.SL_SPECIFIED)
                        height.set(replace_object().sheight().value);
                    else if(replace_object().sheight().specified == SpecialLength.SL_AUTO)
                        height.set(Math.max(150, width.value / 2));
                    else
                        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
                }
                else
                {
                    height.set((int)(width.value / replace_object().ratio()));
                }
            }
            else if(width.specified == SpecialLength.SL_AUTO && height.specified == SpecialLength.SL_SPECIFIED)
            {
                if(replace_object().ratio() < 0)
                {
                    if(replace_object().swidth().specified == SpecialLength.SL_SPECIFIED)
                        width.set(replace_object().swidth().value);
                    else if(replace_object().swidth().specified == SpecialLength.SL_AUTO)
                        width.set(Math.max(300, height.value / 2));
                    else
                        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
                }
                else
                {
                    width.set((int)(height.value * replace_object().ratio()));
                }
            }
            else if(width.specified == SpecialLength.SL_AUTO && height.specified == SpecialLength.SL_AUTO)
            {
                if(replace_object().swidth().specified == SpecialLength.SL_SPECIFIED && replace_object().sheight().specified == SpecialLength.SL_SPECIFIED)
                {
                    width.set(replace_object().swidth().value);
                    height.set(replace_object().sheight().value);
                }
                else if(replace_object().swidth().specified == SpecialLength.SL_SPECIFIED && replace_object().sheight().specified == SpecialLength.SL_AUTO)
                {
                    width.set(replace_object().swidth().value);
                    if(replace_object().ratio() < 0)
                        height.set(Math.max(150, width.value / 2));
                    else
                        height.set((int)(width.value / replace_object().ratio()));
                }
                else if(replace_object().swidth().specified == SpecialLength.SL_AUTO && replace_object().sheight().specified == SpecialLength.SL_SPECIFIED)
                {
                    height.set(replace_object().sheight().value);
                    if(replace_object().ratio() < 0)
                        width.set(Math.max(300, height.value / 2));
                    else
                        width.set((int)(height.value * replace_object().ratio()));
                }
                else if(replace_object().swidth().specified == SpecialLength.SL_AUTO && replace_object().sheight().specified == SpecialLength.SL_AUTO)
                {
                    width.set(300);
                    if(replace_object().ratio() < 0)
                        height.set(Math.max(150, width.value / 2));
                    else
                        height.set((int)(width.value / replace_object().ratio()));
                }
                else
                    throw new BoxError(BoxExceptionType.BET_UNKNOWN);
            }
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }
        else
        {
            if(marginLeft.specified == SpecialLength.SL_AUTO)
                marginLeft.set(0);
            if(marginRight.specified == SpecialLength.SL_AUTO)
                marginRight.set(0);

            if(width.specified == SpecialLength.SL_AUTO)
            {
                width.set(shrink_to_fit(width, marginLeft, marginRight));
                autoWidth=true;
            }
        }
    }

    @Override
    public void calculateHeight(Length height, Length marginTop, Length marginBottom, Length width)
    {
        if(replace_object() != null)
        {
            if(marginTop.specified==SpecialLength.SL_AUTO)
                marginTop.set(0);
            if(marginBottom.specified==SpecialLength.SL_AUTO)
                marginBottom.set(0);

            if(width.specified==SpecialLength.SL_SPECIFIED && height.specified==SpecialLength.SL_SPECIFIED)
            {
                // do nothing
//			size.width=width;
//			size.height=height;
            }
            else if(width.specified==SpecialLength.SL_SPECIFIED && height.specified==SpecialLength.SL_AUTO)
            {
//			size.width=width;

                if(replace_object().ratio() < 0)
                {
                    if(replace_object().sheight().specified==SpecialLength.SL_SPECIFIED)
                        height.set(replace_object().sheight().value);
                    else if(replace_object().sheight().specified==SpecialLength.SL_AUTO)
                        height.set(Math.max(150, width.value / 2));
                    else
                        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
                }
                else
                {
                    height.set((int)(width.value / replace_object().ratio()));
                }
            }
            else if(width.specified==SpecialLength.SL_AUTO && height.specified==SpecialLength.SL_SPECIFIED)
            {
                if(replace_object().ratio() < 0)
                {
                    if(replace_object().swidth().specified == SpecialLength.SL_SPECIFIED)
                        width.set(replace_object().swidth().value);
                    else if(replace_object().swidth().specified==SpecialLength.SL_AUTO)
                        width.set(Math.max(300, height.value / 2));
                    else
                        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
                }
                else
                {
                    width.set((int)(height.value * replace_object().ratio()));
                }
            }
            else if(width.specified == SpecialLength.SL_AUTO && height.specified == SpecialLength.SL_AUTO)
            {
                if(replace_object().swidth().specified == SpecialLength.SL_SPECIFIED && replace_object().sheight().specified == SpecialLength.SL_SPECIFIED)
                {
                    width.set(replace_object().swidth().value);
                    height.set(replace_object().sheight().value);
                }
                else if(replace_object().swidth().specified == SpecialLength.SL_SPECIFIED && replace_object().sheight().specified == SpecialLength.SL_AUTO)
                {
                    width.set(replace_object().swidth().value);
                    if(replace_object().ratio() < 0)
                        height.set(Math.max(150, width.value / 2));
                    else
                        height.set((int)(width.value / replace_object().ratio()));
                }
                else if(replace_object().swidth().specified == SpecialLength.SL_AUTO && replace_object().sheight().specified == SpecialLength.SL_SPECIFIED)
                {
                    height.set(replace_object().sheight().value);
                    if(replace_object().ratio() < 0)
                        width.set(Math.max(300, height.value / 2));
                    else
                        width.set((int)(height.value * replace_object().ratio()));
                }
                else if(replace_object().swidth().specified == SpecialLength.SL_AUTO && replace_object().sheight().specified == SpecialLength.SL_AUTO)
                {
                    width.set(300);
                    if(replace_object().ratio() < 0)
                        height.set(Math.max(150, width.value / 2));
                    else
                        height.set((int)(width.value / replace_object().ratio()));
                }
                else
                    throw new BoxError(BoxExceptionType.BET_UNKNOWN);
            }
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }
        else
        {
            if(marginTop.specified == SpecialLength.SL_AUTO)
                marginTop.set(0);
            if(marginBottom.specified == SpecialLength.SL_AUTO)
                marginBottom.set(0);

            if(height.specified == SpecialLength.SL_AUTO)
            {
                height.set(0);
                autoHeight=true;
            }
        }
    }

	public void setFloated()
	{
		floated=true;
	}

    @Override
    public FlowContext get_flow_context()
    {
        return flowContext;
    }

    @Override
    public Box get_flow_context_box()
    {
        return this;
    }

    @Override
    public int can_stretch(int amount)
    {
        if(_stretchy_cwidth == false)
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);

        if(container().stretchy_cwidth() == true)
            return container().can_stretch(amount);

        int max_stretch=container().contentWidth() -
                width();
//				marginLeft()-marginRight()-
//				borderLeft().width-borderRight().width-
//				paddingLeft()-paddingRight();

        return Math.min(max_stretch, amount);
    }

    @Override
    public boolean stretchy_cwidth()
    {
        return _stretchy_cwidth;
    }

//    @Override
//    public Box resize_height(int from, int amount)
//    {
//        Layable requester=all.get(from);
//        Box earliest_affected=null;
//
//        // force the change in height
//        requester.change_height(amount);
//
//        if(requester.bottom() > contentBottom() && stiffy_height() == false)
//        {
//            change_height(requester.bottom() - contentBottom());
//
////		container().get_flow_context().putback_flow_area(left(), top(), right(), bottom());
//
//            flowContext.reset(contentLeft(), contentRight(), contentTop());
//
//            earliest_affected=container().get_flow_context_box();
//        }
//
////	earliest_affected=container().as_inline_box().inline_root();
////	earliest_affected=0;
//
//        return earliest_affected;
//    }
//
//    @Override
//    public Box resize_width(int from, int amount)
//    {
//        Layable requester=all.get(from);
//        Box earliest_affected;
//
//        // force the change in width
//        requester.change_width(amount);
//
//        if(requester.width() > contentWidth())
//        {
////		container().get_flow_context().putback_flow_area(left(), top(), right(), bottom());
//
//            change_width(requester.width() - contentWidth());
//
//            flowContext.reset(contentLeft(), contentRight(), contentTop());
//
////		earliest_affected=container().get_flow_context_box();
//            earliest_affected=this;
////		earliest_affected=container().as_inline_box().inline_root();
//
//        }
//        else
//            earliest_affected=BoxTypes.toBox(requester);
//
//        return earliest_affected;
//    }
    
    @Override
    public void set_position(int left, int top)
    {
        super.set_position(left, top);

        flowContext.reset(contentLeft(), contentRight(), contentTop());
    }

// returns the width excluding left/right/borders/etc
    private int shrink_to_fit(Length width, Length marginLeft, Length marginRight)
    {
        int pw=preferred_width();
        int pmw=preferred_min_width();
        int avail_width;
        int retval;

        if(stretchy_cwidth())
        {
            avail_width=container().contentWidth() -
                    marginLeft.value - marginRight.value -
                    borderLeft().width - borderRight().width -
                    paddingLeft() - paddingRight();
        }
        else
            avail_width=width.value;

        retval=Math.min(Math.max(pmw, avail_width), pw);
        if(retval == avail_width)
            retval=preferred_shrink_width(avail_width);

        return retval;
    }

    @Override
	public boolean flows()
	{
		return false;
	}

	@Override
	public void setFloatPosition(FloatPosition floatPosition)
	{
		this.floatPosition=floatPosition;
	}
	
    public FloatPosition getFloatPosition()
    {
    	return floatPosition;
    }	
}

