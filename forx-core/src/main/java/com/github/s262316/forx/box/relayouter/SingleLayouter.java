package com.github.s262316.forx.box.relayouter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.Layable;

import com.google.common.collect.Lists;

public class SingleLayouter extends AbstractRelayouter
{
	private final static Logger logger=LoggerFactory.getLogger(SingleLayouter.class);
	
	private Layable last;
	
	public SingleLayouter(Layable last)
	{
		super(new HeightShrinkWrap());
		this.last=last;
	}
	
	@Override
	public List<Layable> getAffected()
	{
		return Lists.newArrayList(last);
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof SingleLayouter))
			return false;

		SingleLayouter rhs=(SingleLayouter)other;
		
		return last==rhs.last;
	}

	
}
