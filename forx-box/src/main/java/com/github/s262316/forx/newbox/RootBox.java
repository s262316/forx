package com.github.s262316.forx.newbox;

import java.awt.Rectangle;

import com.github.s262316.forx.newbox.relayouter.util.LayoutUtils;

public class RootBox extends BlockBox
{
	public RootBox(Visual visual, InterBoxOps interBoxOps)
	{
		super(visual, interBoxOps);
	}

	@Override
	public SizeResult computeDimensions()
	{
		Rectangle r1=getVisual().getGraphicsContext().get_browser_area_limits();
		SizeResult size=new SizeResult(r1.width, r1.height);
		return size;
	}

	public void selfCalculatePosition()
	{
		SizeResult size;
		
		size=computeDimensions();
		setDimensions(size.getWidth(), size.getHeight());

		setPosition(0, 0);
	}
	
	public void selfUncalculatePosition()
	{
		LayoutUtils.invalidatePosition(this);
	}
}
