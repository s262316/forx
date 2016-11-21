package com.github.s262316.forx.box.util;

import com.google.common.base.Objects;

public class Length
{
	public int value;
	public SpecialLength specified;

	public Length(SpecialLength sl)
	{
		this.specified=sl;
		this.value=0;
	}

	public Length(int length)
	{
		this.specified=SpecialLength.SL_SPECIFIED;
		this.value=length;
	}

	public int getInt()
	{
		return value;
	}

        public void set(int value)
        {
		this.specified=SpecialLength.SL_SPECIFIED;
		this.value=value;
        }

	public boolean equals(int num)
	{
		boolean result=false;

		if(specified==SpecialLength.SL_SPECIFIED && value==num)
			result=true;

		return result;
	}

	public boolean equals(SpecialLength sl)
	{
		boolean result=false;

		if(specified==sl)
			result=true;

		return result;
	}

	public boolean notEquals(int num)
	{
		boolean result=false;

		if(specified==SpecialLength.SL_SPECIFIED && value==num)
			result=true;

		return !result;
	}

	boolean notEquals(SpecialLength sl)
	{
		boolean result=false;

		if(specified==sl)
			result=true;

		return !result;
	}

	public void set(Length length)
	{
		value=length.value;
		specified=length.specified;
	}

	public void decrement(Length length)
	{
		value-=length.value;
	}

	@Override
	public String toString()
	{
		return Objects.toStringHelper(this)
	       .addValue(value)
	       .addValue(specified)
	       .toString();
	}
}

