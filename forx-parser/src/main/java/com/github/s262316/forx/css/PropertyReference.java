package com.github.s262316.forx.css;

import com.github.s262316.forx.css.validate.PropertyValidator;
import com.github.s262316.forx.style.Value;

public class PropertyReference
{
	public String name;
	public boolean inherited;
	public Value def;
	private PropertyValidator validator;

	public PropertyReference(String n, boolean i, Value d, PropertyValidator validator)
	{
		name=n;
		inherited=i;
		def=d;
		this.validator=validator;
	}

	public PropertyValidator getValidator()
	{
		return validator;
	}

}
