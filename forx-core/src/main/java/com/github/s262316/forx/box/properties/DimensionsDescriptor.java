package com.github.s262316.forx.box.properties;

import com.github.s262316.forx.box.util.Length;

public class DimensionsDescriptor
{
	public Length width, height;
	public Length minWidth, maxWidth;
	public Length minHeight, maxHeight;

	public DimensionsDescriptor()
	{
		width=new Length(0);
		height=new Length(0);
		minWidth=new Length(0);
		maxWidth=new Length(0);
		minHeight=new Length(0);
		maxHeight=new Length(0);
	}
}

