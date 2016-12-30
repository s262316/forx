package com.github.s262316.forx.box.relayouter;

import java.util.List;

import com.google.common.base.Objects;

import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.relayouter.util.AfterOrEqualsLayable;
import com.github.s262316.forx.box.util.Boxes;

public class MoreWidthLayouter extends AbstractRelayouter
{
	private Layable toResize;
	private Layable cause;
	private int amount;

	public MoreWidthLayouter(Layable toResize, Layable cause, int amount)
	{
		this.toResize=toResize;
		this.cause=cause;
		this.amount=amount;
	}
	
	@Override
	public List<Layable> getAffected()
	{
		return new LayableTreeTraverser()
			.preOrderTraversal(Boxes.root(toResize))
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
		return Objects.toStringHelper(this)
			.add("toResize", toResize)
			.add("cause", cause)
			.add("amount", amount)
			.toString();
	}	
}
