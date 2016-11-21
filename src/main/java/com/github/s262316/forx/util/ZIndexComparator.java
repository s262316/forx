package com.github.s262316.forx.util;

import com.github.s262316.forx.box.AbsoluteBox;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

public class ZIndexComparator extends Ordering<AbsoluteBox>
{
	@Override
	public int compare(AbsoluteBox arg0, AbsoluteBox arg1)
	{
		return Ints.compare(arg0.zIndex(), arg1.zIndex());
	}
}
