/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.style;

import com.google.common.base.Objects;

public class HashValue extends Value
{
    String value;

    public HashValue(String v)
    {
	super(true);
        value=v;
    }

    @Override
    public boolean equals(Object obj)
    {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {return false; }
		
		HashValue rhs = (HashValue) obj;
		
		return Objects.equal(value, rhs.value);
    }

    @Override
    public String toString()
    {
	return "HashValue : " + value + "\n";
    }

    public String str()
    {
	return "#"+value;
    }

}
