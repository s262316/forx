package com.github.s262316.forx.style;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

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
                 .append(Double.doubleToLongBits(amount), Double.doubleToLongBits(rhs.amount))
                 .append(unit, rhs.unit)
                 .isEquals();
	}
}

