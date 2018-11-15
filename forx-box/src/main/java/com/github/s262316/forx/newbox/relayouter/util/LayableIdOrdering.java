package com.github.s262316.forx.newbox.relayouter.util;

import com.github.s262316.forx.newbox.Box;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

public class LayableIdOrdering extends Ordering<Box>
{
	@Override
	public int compare(Box left, Box right)
	{
		return Ints.compare(left.getId(), right.getId());
	}

}
