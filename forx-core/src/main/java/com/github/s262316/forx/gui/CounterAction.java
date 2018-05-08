package com.github.s262316.forx.gui;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class CounterAction
{
	private String name;
	private int amount;

	public CounterAction(String name, int amount)
	{
		this.name = name;
		this.amount = amount;
	}
	
	public String getName()
	{
		return name;
	}
	public int getAmount()
	{
		return amount;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(name, amount);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {return false; }
		
		CounterAction rhs = (CounterAction) obj;
		
		return Objects.equal(name, rhs.name) &&
				Objects.equal(amount, rhs.amount);
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
			.add("name", name)
			.add("amount", amount)
			.toString();
	}
}

