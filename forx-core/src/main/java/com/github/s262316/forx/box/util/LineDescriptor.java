package com.github.s262316.forx.box.util;

public class LineDescriptor
{
	public double lineHeight;
	public VerticalAlignment verticalAlign;

	public LineDescriptor()
	{
		lineHeight=1;
		verticalAlign=new VerticalAlignment(VerticalAlignmentSpecial.VAS_BASELINE);
	}
}
