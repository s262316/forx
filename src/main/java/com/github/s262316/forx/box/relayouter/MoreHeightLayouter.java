package com.github.s262316.forx.box.relayouter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.Layable;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class MoreHeightLayouter extends AbstractRelayouter
{
	private final static Logger logger=LoggerFactory.getLogger(MoreHeightLayouter.class);

	private Layable toResize;
	private Layable cause;
	private int amount;

	public MoreHeightLayouter(Layable toResize, Layable cause, int amount)
	{
		super(new HeightShrinkWrap());
		this.toResize=toResize;
		this.cause=cause;
		this.amount=amount;
	}
	
	@Override
	public List<Layable> getAffected()
	{
		return Lists.newArrayList(toResize, cause);
	}

	@Override
	protected void preLayout()
	{
		toResize.setFutureHeight(toResize.height()+amount);
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof MoreHeightLayouter))
			return false;

		MoreHeightLayouter rhs=(MoreHeightLayouter)other;

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



