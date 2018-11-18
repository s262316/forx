package com.github.s262316.forx.style.selectors;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;

import com.github.s262316.forx.tree.XAttribute;
import com.github.s262316.forx.tree.XElement;
import com.github.s262316.forx.style.selectors.util.Selectors;

import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class SelectorAttr implements Predicate<XElement>
{
    private String attr;
    private String op;
    private String value;
    
    public SelectorAttr(String attr, String op, String value)
    {
		this.attr = attr;
		this.op = op;
		this.value = value;
	}

    @Override
	public boolean apply(XElement e)
    {
        XAttribute a;

        a=e.getAttr(attr);
        if(a==null)
        	return false;
        
        if(op.equals(""))
        {
            // this is enough to match
        	return true;
        }
        else if(op.equals("="))
        {
            // equals
            return value.equals(a.getValue());
        }
        else if(op.equals("|="))
        {
        	return Selectors.dashmatch(value, a.getValue());
        }
        else if(op.equals("~="))
        {
        	Iterable<String> splitAttrValue=Splitter.on(CharMatcher.whitespace())
	        	.trimResults(CharMatcher.whitespace())
	        	.split(a.getValue());
        	
        	return Iterables.contains(splitAttrValue, value);
        }
        else
        {
        	Preconditions.checkState(false, "unknown operator %s", op);
        	return false;
        }
    }

    @Override
    public String toString()
    {
		return MoreObjects.toStringHelper(this)
				.add("attr", attr)
				.add("op", op)
				.add("value", value)
				.toString();
    }
    
	@Override
    public boolean equals(Object obj)
    {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj.getClass() != getClass())
			return false;

		SelectorAttr rhs = (SelectorAttr) obj;
		
		return new EqualsBuilder()
	        .append(attr, rhs.attr)
	        .append(op, rhs.op)
	        .append(value, rhs.value)
	        .isEquals();	
    }
}
