package com.github.s262316.forx.box.properties;

import com.github.s262316.forx.box.util.VerticalAlignment;
import com.github.s262316.forx.box.util.VerticalAlignmentSpecial;

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
