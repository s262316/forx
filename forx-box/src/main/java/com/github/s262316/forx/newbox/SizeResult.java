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

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
			       .add("width",width)
			       .add("height", height)
			       .toString();
	}
}
