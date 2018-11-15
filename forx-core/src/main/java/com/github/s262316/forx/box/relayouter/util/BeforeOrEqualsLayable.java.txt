package com.github.s262316.forx.box.relayouter.util;

import com.github.s262316.forx.box.Layable;

import com.google.common.base.Predicate;

public class BeforeOrEqualsLayable implements Predicate<Layable>
{
	private Layable beforeThis;
	
	public BeforeOrEqualsLayable(Layable beforeThis)
	{
		this.beforeThis=beforeThis;
	}
	
	@Override
	public boolean apply(Layable input)
	{
		return input.getId() <= beforeThis.getId();
	}
}
