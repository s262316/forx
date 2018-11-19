package com.github.s262316.forx.newbox.relayouter.util;

import com.github.s262316.forx.newbox.Box;
import com.google.common.base.Predicate;

public class AfterOrEqualsLayable implements Predicate<Box>
{
	private Box afterThis;
	
	public AfterOrEqualsLayable(Box afterThis)
	{
		this.afterThis=afterThis;
	}
	
	@Override
	public boolean apply(Box input)
	{
		return input.getId() >= afterThis.getId();
	}
}
