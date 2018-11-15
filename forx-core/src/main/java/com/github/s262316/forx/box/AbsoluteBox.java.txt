/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.s262316.forx.box;

import com.github.s262316.forx.box.properties.DimensionsDescriptor;
import com.github.s262316.forx.box.properties.MarginDescriptor;
import com.github.s262316.forx.box.properties.PositionDescriptor;
import com.github.s262316.forx.box.properties.PropertyBoxAdaptor;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.util.Direction;
import com.github.s262316.forx.box.util.Length;
import com.github.s262316.forx.box.util.SpecialLength;

public class AbsoluteBox extends BlockBox
{
    private int sx, sy;
    private int _rel_left, _rel_top, _rel_right, _rel_bottom;
    private int _zIndex;
    private FlowContext flowContext=new FlowContext();
    public boolean _stretchy_cwidth;            //??
    private Box staticContainer;

    public AbsoluteBox(Visual visual)
    {
        super(visual, null, null);
        sx=0;
        sy=0;
    }

    class PositionAndSize
    {
    	DimensionsDescriptor dimensions= new DimensionsDescriptor();
    	PositionDescriptor position= new PositionDescriptor();
    	MarginDescriptor margins = new MarginDescriptor();
    }

    public PositionAndSize calculatePositionAndDimensions()
    {
		PositionAndSize result=new PositionAndSize();

		visual.workOutFlowDimensions(new PropertyBoxAdaptor(this), result.dimensions);
		visual.workOutAbsolutePosition(new PropertyBoxAdaptor(this), result.position);
		visual.computeMarginProperties(new PropertyBoxAdaptor(this), result.margins);
		
		return result;
    }
    
    @Override
    public void set_position(int left, int top)
    {
        super.set_position(left, top);
        flowContext.reset(contentLeft(), contentRight(), contentTop());
    }
    // returns the width excluding left/right/borders/etc

    private int shrink_to_fit(Length left, Length right, Length width, Length marginLeft, Length marginRight)
    {
        int pw=preferred_width();
        int pmw=preferred_min_width();
        int avail_width;
        int retval;
        if(left.specified == SpecialLength.SL_AUTO)
            left.set(0);
        if(right.specified == SpecialLength.SL_AUTO)
            right.set(0);
        if(stretchy_cwidth())
        {
            avail_width=container().contentWidth() -
                    left.value - right.value -
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
    public boolean stretchy_cwidth()
    {
        return _stretchy_cwidth;
    }

    @Override
    public int can_stretch(int amount)
    {
        if(_stretchy_cwidth == false)
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        if(container().stretchy_cwidth() == true)
            return container().can_stretch(amount);
        int max_stretch=container().contentWidth() -
                width() - getRelLeft() - rel_right() - sx;
        //    marginLeft()-marginRight()-
        //    borderLeft().width-borderRight().width-
        //    paddingLeft()-paddingRight();
        return Math.min(max_stretch, amount);
    }

	@Override
	public int canStretchHeight(int amount)
	{
		if(isAutoHeight())
			return amount;
		else
			return 0;
	}  	    
    
//    @Override
//    public Box resize_height(int from, int amount)
//    {
//        Layable requester=all.get(from);
//        // force the change in height
//        requester.change_height(amount);
//        if(requester.bottom() > contentBottom() && stiffy_height() == false)
//            super.change_height(requester.bottom() - contentBottom());
//
//        return null;
//    }
//
//    @Override
//    public Box resize_width(int from, int amount)
//    {
//        Layable requester=all.get(from);
//        Box earliest_affected;
//        // force the change in width
//        requester.change_width(amount);
//        if(requester.width() > contentWidth())
//        {
//            change_width(requester.width() - contentWidth());
//            //  earliest_affected=(*begin_all())->as_box();
//        }
//        // else
//        //  earliest_affected=requester->as_box();
//        earliest_affected=this;
//        flowContext.reset(contentLeft(), contentRight(), contentTop());
//        return earliest_affected;
//    }
    
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

    public void set_relative_position(Length left, Length top, Length right, Length bottom)
    {
        _rel_left=left.value;
        _rel_top=top.value;
        _rel_right=right.value;
        _rel_bottom=bottom.value;
    }

    @Override
    public int contentWidth()
    {
        if(width() == Dimensionable.INVALID)
            return Dimensionable.INVALID;
        return width() - paddingLeft() - paddingRight() - borderLeft().width - borderRight().width -
                marginLeft() - marginRight();
    }

    public int zIndex()
    {
        return _zIndex;
    }

    public void zIndex(int z)
    {
        _zIndex=z;
    }

    @Override
    public boolean calculateWidth(Length width, Length marginLeft, Length marginRight,
            Length left, Length right, Length height)
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
                //   size.width=width;
                //   size.height=height;
            }
            else if(width.specified == SpecialLength.SL_SPECIFIED && height.specified == SpecialLength.SL_AUTO)
            {
                //   size.width=width;
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
        // first, we calculate the static position. this is place an element would have
        // had in normal flow
        // If all three of 'left', 'width', and 'right' are 'auto'

        if(left.specified == SpecialLength.SL_AUTO && right.specified == SpecialLength.SL_AUTO && width.specified == SpecialLength.SL_AUTO)
        {
            // set any 'auto' values for 'margin-left' and 'margin-right' to 0.

            if(marginLeft.specified == SpecialLength.SL_AUTO)
                marginLeft.set(0);
            if(marginRight.specified == SpecialLength.SL_AUTO)
                marginRight.set(0);
            // Then, if the 'direction' property of the containing block is 'ltr'
            // set 'left' to the static position and apply rule number three below;
            if(direction() == Direction.DIR_LTR)
            {

                left.set(sx);

                // 'width' and 'right' are 'auto' and 'left' is not 'auto', then the
                // width is shrink-to-fit . Then solve for 'right'

                width.set(shrink_to_fit(left, right, width, marginLeft, marginRight));
                right.set(container().contentWidth() -
                        left.value - width.value -
                        marginLeft.value - marginRight.value -
                        paddingLeft() - paddingRight() -
                        borderLeft().width - borderRight().width);
            }
            else if(direction() == Direction.DIR_RTL)
            {
                // otherwise, set 'right' to
                // the static position and apply rule number one below.
                right.set(sx);
                // 'left' and 'width' are 'auto' and 'right' is not 'auto', then
                // the width is shrink-to-fit. Then solve for 'left'

                width.set(shrink_to_fit(left, right, width, marginLeft, marginRight));
                left.set(container().contentWidth() -
                        right.value - width.value -
                        marginLeft.value - marginRight.value -
                        borderLeft().width - borderRight().width -
                        paddingLeft() - paddingRight());
            }
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }
        else if(left.specified == SpecialLength.SL_SPECIFIED && right.specified == SpecialLength.SL_SPECIFIED && width.specified == SpecialLength.SL_SPECIFIED)
        {
            int leftrightMargins;
            leftrightMargins=container().contentWidth() -
                    right.value - left.value - width.value -
                    paddingLeft() - paddingRight() -
                    borderLeft().width - borderRight().width;
            // If none of the three is 'auto':
            // If both 'margin-left' and 'margin-right' are 'auto', solve the
            // equation under the extra constraint that the two margins get equal values

            if(marginLeft.specified == SpecialLength.SL_AUTO && marginRight.specified == SpecialLength.SL_AUTO)
            {
                if(leftrightMargins > 0)
                {
                    if(leftrightMargins % 2 == 0)
                    {
                        marginLeft.set(leftrightMargins / 2);
                        marginRight.set(leftrightMargins / 2);
                    }
                    else
                    {
                        marginLeft.set(leftrightMargins / 2);
                        marginRight.set(marginLeft.value + 1);
                    }
                }
                else if(leftrightMargins < 0)
                {
                    // unless this would make them [margins] negative, in which case when direction of the
                    // containing block is 'ltr' ('rtl'), set 'margin-left' ('margin-right') to zero
                    // and solve for 'margin-right' ('margin-left').
                    if(direction() == Direction.DIR_LTR)
                    {

                        marginLeft.set(0);
                        marginRight.set(leftrightMargins);

                    }
                    else if(direction() == Direction.DIR_RTL)
                    {

                        marginRight.set(0);
                        marginLeft.set(leftrightMargins);
                    }
                    else
                        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
                }
                else
                {
                    marginLeft.set(0);
                    marginRight.set(0);
                }
            }
            else if(marginLeft.specified != SpecialLength.SL_AUTO && marginRight.specified != SpecialLength.SL_AUTO)
            {
                // If the values are over-constrained, ignore the value for 'left' (in case the 'direction'
                // property of the containing block is 'rtl') or 'right' (in case 'direction' is 'ltr')
                // and solve for that value.
                if(direction() == Direction.DIR_LTR)
                {

                    right.set(container().contentWidth() -
                            left.value - width.value -
                            marginLeft.value - marginRight.value -
                            paddingLeft() - paddingRight() -
                            borderLeft().width - borderRight().width);
                }
                else if(direction() == Direction.DIR_RTL)
                {

                    left.set(container().contentWidth() -
                            right.value - width.value -
                            marginLeft.value - marginRight.value -
                            borderLeft().width - borderRight().width -
                            paddingLeft() - paddingRight());
                }
                else
                    throw new BoxError(BoxExceptionType.BET_UNKNOWN);
            }
            else if(marginLeft.specified == SpecialLength.SL_AUTO)
            {
                // If one of 'margin-left' or 'margin-right' is 'auto', solve the equation
                // for that value.

                marginLeft.set(leftrightMargins - marginRight.value);
                if(marginLeft.value < 0)
                {
                    // unless this would make them [margins] negative, in which case when direction of the
                    // containing block is 'ltr' ('rtl'), set 'margin-left' ('margin-right') to zero
                    // and solve for 'margin-right' ('margin-left').
                    if(direction() == Direction.DIR_LTR)
                    {

                        marginLeft.set(0);
                        marginRight.set(marginRight.value-leftrightMargins);
                    }
                    else if(direction() == Direction.DIR_RTL)
                    {

                        marginRight.set(0);
                        marginLeft.set(marginRight.value-leftrightMargins);
                    }
                    else
                        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
                }
            }
            else if(marginRight.specified == SpecialLength.SL_AUTO)
            {
                marginRight.set(leftrightMargins - marginLeft.value);
                if(marginRight.value < 0)
                {
                    if(direction() == Direction.DIR_LTR)
                    {
                        marginLeft.set(0);

                        right.set(right.value-leftrightMargins);
                    }
                    else if(direction() == Direction.DIR_RTL)
                    {
                        marginRight.set(0);
                        left.set(left.value-leftrightMargins);
                    }
                    else
                        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
                }
            }
        }
        else
        {
            // set any 'auto' values for 'margin-left' and 'margin-right' to 0.

            if(marginLeft.specified == SpecialLength.SL_AUTO)
                marginLeft.set(0);
            if(marginRight.specified == SpecialLength.SL_AUTO)
                marginRight.set(0);
            // Otherwise:
            if(left.specified == SpecialLength.SL_AUTO && width.specified == SpecialLength.SL_AUTO && right.specified == SpecialLength.SL_SPECIFIED)
            {
                // 1. 'left' and 'width' are 'auto' and 'right' is not 'auto', then the width
                //    is shrink-to-fit. Then solve for 'left'
                if(this.getFutureWidth().isPresent())
    				width.set(this.getFutureWidth().get() - marginLeft.getInt() - marginRight.getInt());
                else
                	width.set(shrink_to_fit(left, right, width, marginLeft, marginRight));
                
                left.set(container().contentWidth() -
                        right.value - width.value - marginLeft.value - marginRight.value -
                        marginLeft.value - borderLeft().width - paddingLeft() -
                        marginRight.value - borderRight().width - paddingRight());
            }
            else if(left.specified==SpecialLength.SL_AUTO && right.specified==SpecialLength.SL_AUTO && width.specified==SpecialLength.SL_SPECIFIED)
            {
                // 2. 'left' and 'right' are 'auto' and 'width' is not 'auto', then if the
                //    'direction' property of the containing block is 'ltr' set 'left' to the
                //    static position, otherwise set 'right' to the static position. Then solve
                //    for 'left' (if 'direction is 'rtl') or 'right' (if 'direction' is 'ltr').
                if(direction() == Direction.DIR_LTR)
                {
                    left.set(sx);
                    right.set(container().contentWidth() -
                            left.value -
                            marginLeft.value - marginRight.value -
                            paddingLeft() - paddingRight() -
                            borderLeft().width - borderRight().width);
                }
                else if(direction() == Direction.DIR_RTL)
                {
                    right.set(sx);
                    left.set(container().contentWidth() -
                            right.value -
                            marginLeft.value - marginRight.value -
                            paddingLeft() - paddingRight() -
                            borderLeft().width - borderRight().width);
                }
                else
                    throw new BoxError(BoxExceptionType.BET_UNKNOWN);
            }
            else if(width.specified==SpecialLength.SL_AUTO && right.specified==SpecialLength.SL_AUTO && left.specified==SpecialLength.SL_SPECIFIED)
            {
                // 3. 'width' and 'right' are 'auto' and 'left' is not 'auto', then the width is
                //    shrink-to-fit . Then solve for 'right'
                if(this.getFutureWidth().isPresent())
    				width.set(this.getFutureWidth().get() - marginLeft.getInt() - marginRight.getInt());
                else
                	width.set(shrink_to_fit(left, right, width, marginLeft, marginRight));
                
                right.set(container().contentWidth() -
                        left.value - width.value -
                        marginLeft.value - marginRight.value -
                        paddingLeft() - paddingRight() -
                        borderLeft().width - borderRight().width);
            }
            else if(left.specified==SpecialLength.SL_AUTO && width.specified==SpecialLength.SL_SPECIFIED && right.specified==SpecialLength.SL_SPECIFIED)
            {
                // 4. 'left' is 'auto', 'width' and 'right' are not 'auto', then solve for 'left'
                left.set(container().contentWidth() -
                        right.value - width.value -
                        marginLeft.value - marginRight.value -
                        paddingLeft() - paddingRight() -
                        borderLeft().width - borderRight().width);
            }
            else if(width.specified==SpecialLength.SL_AUTO && left.specified==SpecialLength.SL_SPECIFIED && right.specified==SpecialLength.SL_SPECIFIED)
            {
                // 5. 'width' is 'auto', 'left' and 'right' are not 'auto', then solve for 'width'
                width.set(container().contentWidth() -
                        right.value - left.value -
                        marginLeft.value - marginRight.value -
                        paddingLeft() - paddingRight() -
                        borderLeft().width - borderRight().width);
            }
            else if(right.specified==SpecialLength.SL_AUTO && left.specified==SpecialLength.SL_SPECIFIED && width.specified==SpecialLength.SL_SPECIFIED)
            {
                // 6. 'right' is 'auto', 'left' and 'width' are not 'auto', then solve for 'right'
                right.set(container().contentWidth() -
                        left.value -
                        marginLeft.value + marginRight.value -
                        paddingLeft() - paddingRight() -
                        borderLeft().width - borderRight().width);
            }
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }

        return false;
    }

    public void calculateHeight(Length height, Length marginTop, Length marginBottom,
            Length top, Length bottom, Length width)
    {
        //if(replace_object)
        //{
        // // 1. The used value of 'height' is determined as for inline replaced elements.

        // if(height.specified=SpecialLength.SL_AUTO && width.specified=SpecialLength.SL_AUTO && replace_object.height()!=SpecialLength.SL_AUTO) // (h1)

        // {
        //  // If 'height' has a computed value of 'auto' and 'width' also has a computed value of 'auto',
        //  // the element's intrinsic height is the used value of 'height', it has one.
        //  height=replace_object.height();
        // }

        // else if((height.specified=SpecialLength.SL_AUTO && width!=SpecialLength.SL_AUTO && replace_object.ratio()!=R_NONE) ||
        //   (height.specified=SpecialLength.SL_AUTO && width.specified=SpecialLength.SL_AUTO && replace_object.height()==SpecialLength.SL_AUTO &&
        //   replace_object.width()!=SpecialLength.SL_AUTO && replace_object.ratio()!=R_NONE)) // (h2)

        // {
        //  // If 'height' has a computed value of 'auto' and 'width' has some other computed value, and the
        //  // replaced element has an intrinsic ratio, or, if both 'height' and 'width' have computed
        //  // values of 'auto', and the element has no intrinsic height but does have an intrinsic width
        //  // and intrinsic ratio, then the used value of 'height' is:
        //  // (used width) / (intrinsic ratio)
        //  height=replace_object.width()/replace_object.ratio();
        // }

        // else if(height.specified=SpecialLength.SL_AUTO) // (h3)

        // {
        //  // Otherwise, if 'height' has a computed value of 'auto', but none of the conditions above are
        //  // met, then the used value of 'height' must be set to 150px. If 300px is too wide to fit the
        //  // device width, UAs should use the height of the largest rectangle that has a 2:1 ratio and fits
        //  // the device width instead.
        //  height=width/2;
        // }
        // // For 'inline' and 'inline-block' elements, the margin box is used when calculating the
        // // height of the line box. This value already includes the margins... watch out when positioning though!
        // replace_object.use_height(height);
        // // 2. If both 'top' and 'bottom' have the value 'auto', replace 'top' with the
        // // element's static position.

        // if(top.specified=SpecialLength.SL_AUTO && bottom.specified=SpecialLength.SL_AUTO)

        //  top=sx;
        // // 3. If 'bottom' is 'auto', replace any 'auto' on 'margin-top' or 'margin-bottom' with '0'.

        // if(bottom.specified=SpecialLength.SL_AUTO)
        // {
        //  if(margintop.specified=SpecialLength.SL_AUTO)
        //   marginTop=0;
        //  if(marginbottom.specified=SpecialLength.SL_AUTO)

        //   marginBottom=0;
        // }
        // // 4. If at this point both 'margin-top' and 'margin-bottom' are still 'auto', solve
        // // the equation under the extra constraint that the two margins must get equal values.

        // if(margintop.specified=SpecialLength.SL_AUTO && marginbottom.specified=SpecialLength.SL_AUTO)

        // {
        //  int leftover;
        //  leftover=container().contentHeight()-
        //    top-bottom-paddingTop()-paddingBottom()-
        //    borderTop().width-borderBottom().width-
        //    height;
        //  if(leftover>0)
        //  {
        //   if(leftover%2==0)
        //    marginTop=marginBottom=leftover/2;
        //   else
        //   {
        //    marginTop=leftover/2;
        //    marginBottom=(leftover/2)+1;
        //   }
        //  }
        //  else
        //  {
        //   marginTop=marginBottom=0;
        //  }
        // }
        // // 5. If at this point there is only one 'auto' left, solve the equation for that value.

        // if(top.specified=SpecialLength.SL_AUTO)
        // {
        //  top=bottom-height;
        // }
        // else if(bottom.specified=SpecialLength.SL_AUTO)

        // {
        //  bottom=container().contentWidth()-
        //    top-height-
        //    marginTop-marginBottom-
        //    paddingTop()-paddingBottom()-
        //    borderTop().width-borderBottom().width;
        // }

        // else if(height.specified=SpecialLength.SL_AUTO)

        // {
        //  height=container().contentWidth()-
        //    top-bottom-
        //    marginTop-marginBottom-
        //    paddingTop()-paddingBottom()-
        //    borderTop().width-borderBottom().width;
        // }

        // else if(margintop.specified=SpecialLength.SL_AUTO)

        // {
        //  marginTop=container().contentWidth()-
        //    top-bottom-height-
        //    marginBottom-
        //    paddingTop()-paddingBottom()-
        //    borderTop().width-borderBottom().width;
        // }

        // else if(marginbottom.specified=SpecialLength.SL_AUTO)

        // {
        //  marginBottom=container().contentWidth()-
        //    top-bottom-height-
        //    marginTop-
        //    paddingTop()-paddingBottom()-
        //    borderTop().width-borderBottom().width;
        // }
        // // 6. If at this point the values are over-constrained, ignore the value for 'bottom' and solve for that value.
        // if(container().contentWidth()!=
        //  top+bottom+
        //  height+
        //  marginTop+marginBottom+
        //  paddingTop()+paddingBottom()+
        //  borderTop().width+borderBottom().width)
        // {
        //  bottom=top+height+
        //    marginTop+marginBottom+
        //    paddingTop()+paddingBottom()+
        //    borderTop().width+borderBottom().width;
        // }
        //}
        //else
        //{

        if(top.specified==SpecialLength.SL_AUTO && bottom.specified==SpecialLength.SL_AUTO && height.specified==SpecialLength.SL_AUTO)
        {
            // If all three of 'top', 'height', and 'bottom' are auto, set 'top' to
            // the static position and apply rule number three below.

            top.set(sy);

            // 3. 'height' and 'bottom' are 'auto' and 'top' is not 'auto', then the
            // height is based on the content, set 'auto' values for 'margin-top' and
            // 'margin-bottom' to 0, and solve for 'bottom'
            
            if(this.getFutureHeight().isPresent())
				height.set(this.getFutureHeight().get());
            
        }
        else if(top.specified==SpecialLength.SL_SPECIFIED && bottom.specified==SpecialLength.SL_SPECIFIED && height.specified==SpecialLength.SL_SPECIFIED)
        {
            // If none of the three are 'auto'

            if(marginTop.specified==SpecialLength.SL_AUTO && marginBottom.specified==SpecialLength.SL_AUTO)
            {
                // If both 'margin-top' and 'margin-bottom' are 'auto', solve the
                // equation under the extra constraint that the two margins get equal values
                int leftover;
                leftover=container().contentHeight() -
                        top.value - bottom.value - paddingTop() - paddingBottom() -
                        borderTop().width - borderBottom().width -
                        height.value;

                if(leftover > 0)
                {
                    if(leftover % 2 == 0)
                    {
                        marginTop.set(leftover / 2);
                        marginBottom.set(leftover / 2);
                    }
                    else
                    {
                        marginTop.set(leftover / 2);
                        marginBottom.set((leftover / 2) + 1);
                    }
                }
                else if(leftover == 0)
                {
                    marginTop.set(0);
                    marginBottom.set(0);

                }
                else
                {
                    // If the values are over-constrained, ignore the value for 'bottom'
                    // and solve for that value
                    marginTop.set(0);
                    marginBottom.set(0);

                    bottom.set(container().contentHeight() -
                            top.value - paddingTop() - paddingBottom() -
                            borderTop().width - borderBottom().width -
                            marginTop.value - marginBottom.value -
                            height.value);
                }
            }
            else if(marginTop.specified != SpecialLength.SL_AUTO && marginBottom.specified != SpecialLength.SL_AUTO)
            {
                // If the values are over-constrained, ignore the value for
                // 'bottom' and solve for that value.

                bottom.set(container().contentHeight() -
                        top.value - paddingTop() - paddingBottom() -
                        borderTop().width - borderBottom().width -
                        marginTop.value - marginBottom.value -
                        height.value);
            }
            else if(marginTop.specified==SpecialLength.SL_AUTO)
            {
                // If one of 'margin-top' or 'margin-bottom' is 'auto', solve the
                // equation for that value.

                marginTop.set(container().contentHeight() -
                        top.value - bottom.value - paddingTop() - paddingBottom() -
                        borderTop().width - borderBottom().width -
                        marginBottom.value -
                        height.value);
            }
            else if(marginBottom.specified==SpecialLength.SL_AUTO)
            {
                // If one of 'margin-top' or 'margin-bottom' is 'auto', solve the
                // equation for that value.

                marginBottom.set(container().contentHeight() -
                        top.value - bottom.value - paddingTop() - paddingBottom() -
                        borderTop().width - borderBottom().width -
                        marginTop.value -
                        height.value);
            }
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);

        }
        else
        {
            // Otherwise, pick the one of the following six rules that applies.

            if(top.specified==SpecialLength.SL_AUTO && bottom.specified==SpecialLength.SL_SPECIFIED && height.specified==SpecialLength.SL_AUTO)
            {
                // 1. 'top' and 'height' are 'auto' and 'bottom' is not 'auto', then
                // the height is based on the content, set 'auto' values for 'margin-top'
                // and 'margin-bottom' to 0, and solve for 'top'

                super.calculateHeight(height, marginTop, marginBottom, width);
                top.set(container().contentHeight() -
                        bottom.value - paddingTop() - paddingBottom() -
                        borderTop().width - borderBottom().width -
                        marginTop.value - marginBottom.value -
                        height.value);
            }
            else if(top.specified==SpecialLength.SL_AUTO && bottom.specified==SpecialLength.SL_AUTO && height.specified==SpecialLength.SL_SPECIFIED)
            {
                // 2. 'top' and 'bottom' are 'auto' and 'height' is not 'auto', then set
                // 'top' to the static position, set 'auto' values for 'margin-top' and
                // 'margin-bottom' to 0, and solve for 'bottom'
                top.set(sy);

                if(marginTop.specified==SpecialLength.SL_AUTO)
                    marginTop.set(0);
                if(marginBottom.specified==SpecialLength.SL_AUTO)
                    marginBottom.set(0);
                bottom.set(container().contentHeight() -
                        top.value - paddingTop() - paddingBottom() -
                        borderTop().width - borderBottom().width -
                        marginTop.value - marginBottom.value -
                        height.value);
            }
            else if(top.specified==SpecialLength.SL_SPECIFIED && bottom.specified==SpecialLength.SL_AUTO && height.specified==SpecialLength.SL_AUTO)
            {
                // 3. 'height' and 'bottom' are 'auto' and 'top' is not 'auto', then the
                // height is based on the content, set 'auto' values for 'margin-top' and
                // 'margin-bottom' to 0, and solve for 'bottom'

                if(marginTop.specified==SpecialLength.SL_AUTO)
                    marginTop.set(0);
                if(marginBottom.specified==SpecialLength.SL_AUTO)
                    marginBottom.set(0);

                super.calculateHeight(height, marginTop, marginBottom, width);

                bottom.set(container().contentHeight() -
                        top.value - paddingTop() - paddingBottom() -
                        borderTop().width - borderBottom().width -
                        marginTop.value - marginBottom.value -
                        height.value);
            }
            else if(top.specified==SpecialLength.SL_AUTO && bottom.specified==SpecialLength.SL_SPECIFIED && height.specified==SpecialLength.SL_SPECIFIED)
            {
                // 4. 'top' is 'auto', 'height' and 'bottom' are not 'auto', then set 'auto'
                // values for 'margin-top' and 'margin-bottom' to 0, and solve for 'top'

                if(marginTop.specified==SpecialLength.SL_AUTO)
                    marginTop.set(0);
                if(marginBottom.specified==SpecialLength.SL_AUTO)
                    marginBottom.set(0);
                top.set(container().contentHeight() -
                        bottom.value - paddingTop() - paddingBottom() -
                        borderTop().width - borderBottom().width -
                        marginTop.value - marginBottom.value -
                        height.value);
            }
            else if(top.specified==SpecialLength.SL_SPECIFIED && bottom.specified==SpecialLength.SL_SPECIFIED && height.specified==SpecialLength.SL_AUTO)
            {
                // 5. 'height' is 'auto', 'top' and 'bottom' are not 'auto', then 'auto'
                // values for 'margin-top' and 'margin-bottom' are set to 0 and solve for
                // 'height'

                if(marginTop.specified==SpecialLength.SL_AUTO)
                    marginTop.set(0);
                if(marginBottom.specified==SpecialLength.SL_AUTO)
                    marginBottom.set(0);
                height.set(container().contentHeight() -
                        top.value - bottom.value - paddingTop() - paddingBottom() -
                        borderTop().width - borderBottom().width -
                        marginTop.value - marginBottom.value);
            }
            else if(top.specified==SpecialLength.SL_SPECIFIED && bottom.specified==SpecialLength.SL_AUTO && height.specified==SpecialLength.SL_SPECIFIED)
            {
                // 6. 'bottom' is 'auto', 'top' and 'height' are not 'auto', then set 'auto'
                // values for 'margin-top' and 'margin-bottom' to 0 and solve for 'bottom'

                if(marginTop.specified==SpecialLength.SL_AUTO)
                    marginTop.set(0);
                if(marginBottom.specified==SpecialLength.SL_AUTO)
                    marginBottom.set(0);
                bottom.set(container().contentHeight() -
                        top.value - paddingTop() - paddingBottom() -
                        borderTop().width - borderBottom().width -
                        marginTop.value - marginBottom.value -
                        height.value);
            }
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }
        if(height.specified==SpecialLength.SL_AUTO)
        {
            autoHeight=true;
        }
    }

    @Override
    public int getRelLeft()
    {
        return _rel_left;
    }

    public int rel_right()
    {
        return _rel_right;
    }

    @Override
    public int getRelTop()
    {
        return _rel_top;
    }

    public int rel_bottom()
    {
        return _rel_bottom;
    }

    public void set_static_position(int left, int top)
    {
        sx=left;
        sy=top;
    }

    @Override
	public boolean flows()
	{
		return false;
	}

	public void setStaticContainer(Box staticContainer)
	{
		this.staticContainer = staticContainer;
	}

	public Box getStaticContainer()
	{
		return staticContainer;
	}


}
