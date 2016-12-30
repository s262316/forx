package com.github.s262316.forx.util;

import com.github.s262316.forx.box.Layable;

import com.google.common.base.Function;

public class ExtractBoxId implements Function<Layable, Integer>
{
	@Override
	public Integer apply(Layable lay)
	{
		return lay.getId();
	}
}
