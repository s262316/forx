package com.github.s262316.forx.box.relayouter.util;

import com.github.s262316.forx.box.Layable;

import com.google.common.base.Predicate;

public class AfterOrEqualsLayable implements Predicate<Layable>
{
	private Layable afterThis;
	
	public AfterOrEqualsLayable(Layable afterThis)
	{
		this.afterThis=afterThis;
	}
	
	@Override
	public boolean apply(Layable input)
	{
		return input.getId() >= afterThis.getId();
	}
}
