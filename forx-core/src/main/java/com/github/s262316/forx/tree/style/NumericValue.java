package com.github.s262316.forx.tree.style;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.github.s262316.forx.box.properties.PropertyAdaptor;
import com.google.common.base.Preconditions;

public class NumericValue extends Value
{
    public double amount;
	public String unit;

    public NumericValue(double amount, String u)
    {
        super(true);
        this.amount=amount;
        this.unit=u;
    }
    
    public int absLength(PropertyAdaptor on)
    {
		switch(unit)
		{
		case "em":
			return (int)(on.getContainer().getFont().getSize()*amount);
		case "ex":
			return (int)(on.getContainer().getFont().getSize()*amount);
    	case "px":
    		return (int)amount;
    	case "pt":
    		return (int)amount;
    	case "in":
			return (int)(amount/96);
    	case "cm":
    		return (int)((25.54*amount)/96);
    	case "mm":
			return (int)((2.54*amount)/96);
		default:
			Preconditions.checkState(false, "unknown unit %s", unit);
			return 0; // never reached			
		}
    }

	@Override
	public int hashCode()
	{
		return Objects.hash(amount, unit);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass())
		{
			return false;
		}		
		
		NumericValue rhs = (NumericValue) obj;
		return new EqualsBuilder()
                 .append(amount, rhs.amount)
                 .append(unit, rhs.unit)
                 .isEquals();
	}
}
