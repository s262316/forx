package com.github.s262316.forx.newbox;

import com.google.common.base.MoreObjects;

public class SizeResult
{
	private int width, height;

	public SizeResult(int width, int height)
	{
		this.width=width;
		this.height=height;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
			       .add("width",width)
			       .add("height", height)
			       .toString();
	}
}
