package com.github.s262316.forx.box.adders;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Inline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * adds a box to its parent
 *
 */
public class DefaultAdder implements Adder
{
	private final static Logger logger= LoggerFactory.getLogger(DefaultAdder.class);

	private Box subject;
	
	public DefaultAdder(Box subject)
	{
		this.subject=subject;
	}
	
	@Override
	public void add(Box newChild)
	{
		logger.debug("add {} -> {}", subject, newChild);
		subject.flow_back(newChild);
	}

	@Override
	public void add(Inline newChild)
	{
		logger.debug("add {} -> {}", subject, newChild);
		subject.flow_back(newChild);
	}
}
