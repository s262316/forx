package com.github.s262316.forx.box.properties.converters;

import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.Value;

import com.google.common.base.Preconditions;

public class EnumConverter<MT extends Enum<MT>> implements ModelToBoxConverter<MT>
{
	private String prefix;
	private boolean capitalize;
	private Class<MT> enumClazz;

	public EnumConverter(String prefix, boolean capitalize, Class<MT> enumClazz)
	{
		this.prefix = prefix;
		this.capitalize = capitalize;
		this.enumClazz=enumClazz;
	}

	@Override
	public MT convert(Value value)
	{
		Preconditions.checkArgument(value instanceof Identifier);
		
		Identifier ident=(Identifier)value;
		String enumText=prefix+ident.ident;
		
		if(capitalize)
			enumText=enumText.toUpperCase();
				
		MT modelValue=Enum.valueOf(enumClazz, enumText);

		return modelValue;
	}
}
