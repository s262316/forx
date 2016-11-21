package com.github.s262316.forx.box.relayouter;

import com.github.s262316.forx.box.Layable;

public class DefaultLayableContainer implements LayableContainer
{
	private Layable container;
	
	public DefaultLayableContainer(Layable member)
	{
		this.container=member.container();
	}
	
	@Override
	public LayoutResult calculatePosition(Layable member)
	{
		return container.calculate_position(member);
	}

	@Override
	public void uncalculatePosition(Layable member)
	{
		container.uncalculate_position(member);
	}

	@Override
	public Layable getContainer()
	{
		return container;
	}
}

