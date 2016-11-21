package com.github.s262316.forx.cast;

import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Inline;

public class LayableTypes
{
	public static boolean isBox(Layable lay)
	{
		return toBox(lay)!=null;
	}

	public static boolean isInline(Layable lay)
	{
		return toInline(lay)!=null;
	}

	public static Box toBox(Layable lay)
	{
		return (Box)lay;
	}

	public static Inline toInline(Layable lay)
	{
		return (Inline)lay;
	}
}
