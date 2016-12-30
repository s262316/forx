package com.github.s262316.forx.tree.style.selectors;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.google.common.base.Objects;

public class Specificity
{
    private int a, b, c, d;

    public Specificity(int a, int b, int c, int d)
    {
        this.a=a;
        this.b=b;
        this.c=c;
        this.d=d;
    }

    public int getA()
    {
		return a;
	}

	public int getB()
	{
		return b;
	}

	public int getC()
	{
		return c;
	}

	public int getD()
	{
		return d;
	}

	@Override
    public boolean equals(Object obj)
    {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj.getClass() != getClass())
			return false;

		Specificity rhs = (Specificity) obj;
		
		return new EqualsBuilder()
	        .append(a, rhs.a)
	        .append(b, rhs.b)
	        .append(c, rhs.c)
	        .append(d, rhs.d)
	        .isEquals();	
    }

	@Override
	public String toString()
	{
		return Objects.toStringHelper(this)
			.addValue(a)
			.addValue(b)
			.addValue(c)
			.addValue(d)
			.toString();
	}
}
