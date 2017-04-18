package com.github.s262316.forx.css;

import com.github.s262316.forx.css.validate.PropertyExpander;

public class ShorthandPropertyReference
{
	private String name;
	private PropertyExpander expander;
	
	public ShorthandPropertyReference(String name, PropertyExpander expander)
	{
		this.name = name;
		this.expander = expander;
	}

	public String getName()
	{
		return name;
	}

	public PropertyExpander getExpander()
	{
		return expander;
	}
}
