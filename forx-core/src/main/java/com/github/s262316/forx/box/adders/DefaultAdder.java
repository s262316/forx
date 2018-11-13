package com.github.s262316.forx.box.adders;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Inline;

/**
 * adds a box to its parent
 *
 */
public class DefaultAdder implements Adder
{
	private Box subject;
	
	public DefaultAdder(Box subject)
	{
		this.subject=subject;
	}
	
	@Override
	public void add(Box newChild)
	{
		subject.flow_back(newChild);
	}

	@Override
	public void add(Inline newChild)
	{
		subject.flow_back(newChild);
	}
}
