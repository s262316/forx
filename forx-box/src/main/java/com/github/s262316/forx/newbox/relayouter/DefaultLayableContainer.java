package com.github.s262316.forx.newbox.relayouter;

import com.github.s262316.forx.newbox.Box;

public class DefaultLayableContainer implements LayableContainer
{
	private Box container;
	
	public DefaultLayableContainer(Box member)
	{
		this.container=member.getInterBoxOps().getContainer();
	}
	
	@Override
	public LayoutResult calculatePosition(Box member)
	{
		return container.calculatePosition(member.getInterBoxOps().getSubject());
	}

	@Override
	public void uncalculatePosition(Box member)
	{
		container.uncalculatePosition(member.getInterBoxOps().getSubject());
	}

	@Override
	public Box getContainer()
	{
		return container;
	}
}

