package com.github.s262316.forx.box.properties;

import com.github.s262316.forx.box.util.Length;

public class MarginDescriptor
{
	public Length left, right;
	public Length top, bottom;

	public MarginDescriptor()
	{
		left=new Length(0);
		right=new Length(0);
		top=new Length(0);
		bottom=new Length(0);
	}
}

