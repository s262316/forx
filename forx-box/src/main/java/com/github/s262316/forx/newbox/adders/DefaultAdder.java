package com.github.s262316.forx.newbox.adders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.newbox.Box;
import com.github.s262316.forx.newbox.Inline;
import com.github.s262316.forx.newbox.PropertiesEndPoint;

/**
 * adds a box to its parent
 *
 */
public class DefaultAdder implements Adder
{
	private final static Logger logger= LoggerFactory.getLogger(DefaultAdder.class);

	private PropertiesEndPoint subject;
	
	public DefaultAdder(PropertiesEndPoint subject)
	{
		this.subject=subject;
	}
	
	@Override
	public void add(Box newChild)
	{
		logger.debug("add {} -> {}", subject, newChild);
		subject.flow(newChild);
	}

	@Override
	public void add(Inline newChild)
	{
		logger.debug("add {} -> {}", subject, newChild);
		subject.flow(newChild);
	}
}
