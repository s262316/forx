package com.github.s262316.forx.css;

import java.awt.Font;

import com.github.s262316.forx.box.properties.PropertyAdaptor;

public class DummyPropertyAdaptor implements PropertyAdaptor
{
	@Override
	public int contentWidth()
	{
		return 0;
	}

	@Override
	public int contentHeight()
	{
		return 0;
	}

	@Override
	public PropertyAdaptor getContainer()
	{
		return this;
	}

	@Override
	public Font getFont()
	{
		return null;
	}
}
