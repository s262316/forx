package com.github.s262316.forx.tree.style;

import com.github.s262316.forx.tree.events2.XmlMouseEvent;

import com.google.common.base.Objects;

public class Identifier extends Value
{
    public String ident;

    public Identifier()
    {
        super(false);
    }

    public Identifier(String id)
    {
        super(true);
        ident=id;
    }

	@Override
	public String toString()
	{
		return Objects.toStringHelper(this)
			.add("ident", ident)
			.toString();
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(ident);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {return false; }
		
		Identifier rhs = (Identifier) obj;
		
		return Objects.equal(ident, rhs.ident);
	}	
}

