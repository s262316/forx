package com.github.s262316.forx.box.relayouter.util;

import com.github.s262316.forx.box.Layable;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

public class LayableIdOrdering extends Ordering<Layable>
{
	@Override
	public int compare(Layable left, Layable right)
	{
		return Ints.compare(left.getId(), right.getId());
	}

}
