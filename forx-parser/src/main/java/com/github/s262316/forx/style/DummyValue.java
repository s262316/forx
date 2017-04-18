package com.github.s262316.forx.style;


public class DummyValue extends Value
{
	public DummyValue()
	{
		super(false);
	}
	
	@Override
    public boolean equals(Object obj)
    {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj.getClass() != getClass())
			return false;

		return true;
    }	
}
