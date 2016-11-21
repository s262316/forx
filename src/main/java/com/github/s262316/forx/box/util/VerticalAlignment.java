package com.github.s262316.forx.box.util;

public class VerticalAlignment
{
	public int value;
	public VerticalAlignmentSpecial specified;

	public VerticalAlignment()
	{}

	public VerticalAlignment(VerticalAlignmentSpecial sl)
	{
		specified=sl;
		value=0;
	}

	public VerticalAlignment(int length)
	{
		specified=VerticalAlignmentSpecial.VAS_SPECIFIED;
		value=length;
	}
}
