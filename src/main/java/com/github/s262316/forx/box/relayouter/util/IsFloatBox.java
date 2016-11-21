package com.github.s262316.forx.box.relayouter.util;

import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.cast.BoxTypes;

import com.google.common.base.Predicate;

public class IsFloatBox implements Predicate<Layable>
{
	@Override
	public boolean apply(Layable lay)
	{
		return BoxTypes.isFloatBox(lay);
	}
}
