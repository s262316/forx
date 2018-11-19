package com.github.s262316.forx.newbox.relayouter;

import java.util.List;

import com.github.s262316.forx.newbox.Box;
import com.github.s262316.forx.newbox.relayouter.util.AfterOrEqualsLayable;
import com.github.s262316.forx.newbox.util.Boxes;
import com.google.common.base.MoreObjects;

public class MoreWidthLayouter extends AbstractRelayouter
{
	private Box toResize;
	private Box cause;
	private int amount;

	public MoreWidthLayouter(Box toResize, Box cause, int amount)
	{
		this.toResize=toResize;
		this.cause=cause;
		this.amount=amount;
	}
	
	@Override
	public List<Box> getAffected()
	{
		return new LayableTreeTraverser()
			.preOrderTraversal(toResize.getInterBoxOps().getRoot())
			.filter(new AfterOrEqualsLayable(toResize))
			.toList();
	}

	@Override
	protected void preLayout()
	{
		toResize.setFutureWidth(toResize.width()+amount);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof MoreWidthLayouter))
			return false;

		MoreWidthLayouter rhs=(MoreWidthLayouter)other;

		return toResize==rhs.toResize &&
				cause==rhs.cause &&
				amount==rhs.amount;
	}
	
	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
			.add("toResize", toResize)
			.add("cause", cause)
			.add("amount", amount)
			.toString();
	}	
}
