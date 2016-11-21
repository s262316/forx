package com.github.s262316.forx.box;

import java.awt.Rectangle;

import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.util.Length;
import com.github.s262316.forx.box.util.FloatPosition;

public class InlineBlockRootBox extends BlockBox
{
    private FlowContext flowContext;

	public InlineBlockRootBox(Visual v)
	{
		super(v, null, null);
	}

	// returns the width excluding left/right/borders/etc
	public int shrink_to_fit(Length width, Length marginLeft, Length marginRight)
	{
		int pw=preferred_width();
		int pmw=preferred_min_width();
		int avail_width;
		int retval;

		if(stretchy_cwidth())
		{
			avail_width=container().contentWidth()-
				marginLeft.value-marginRight.value-
				borderLeft().width-borderRight().width-
				paddingLeft()-paddingRight();
		}
		else
			avail_width=width.value;

		retval=Math.min(Math.max(pmw, avail_width), pw);
		if(retval==avail_width)
			retval=preferred_shrink_width(avail_width);

		return retval;
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
	public void set_position(int left, int top)
	{
		super.set_position(left, top);

		flowContext.reset(contentLeft(), contentRight(), contentTop());
	}

    @Override
	public boolean flows()
	{
		return false;
	}

}
