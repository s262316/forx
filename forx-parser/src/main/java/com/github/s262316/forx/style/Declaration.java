package com.github.s262316.forx.style;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;

import com.google.common.base.Objects;

public class Declaration
{
    private String property;
    private Value value;
    private boolean important;

    public Declaration(String p, Value v, boolean i)
    {
    	property=p;
        value=v;
        important=i;
    }

    public String getProperty()
	{
		return property;
	}

	public Value getValue()
	{
		return value;
	}

	public boolean isImportant()
	{
		return important;
	}

	@Override
    public boolean equals(Object obj)
    {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj.getClass() != getClass())
			return false;

		Declaration rhs = (Declaration) obj;
		
		return new EqualsBuilder()
	        .append(property, rhs.property)
	        .append(value, rhs.value)
	        .append(important, rhs.important)
	        .isEquals();	
    }

    @Override
    public String toString()
    {
		return MoreObjects.toStringHelper(this)
				.add("property", property)
				.add("value", value)
				.add("important", important)
				.toString();
    }


}
