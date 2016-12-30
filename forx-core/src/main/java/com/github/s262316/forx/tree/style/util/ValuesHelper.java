package com.github.s262316.forx.tree.style.util;

import org.apache.commons.lang3.ObjectUtils;

import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.NumericValue;
import com.github.s262316.forx.tree.style.StringValue;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class ValuesHelper
{
	public static Optional<String> getIdentifier(Value value)
	{
		if(ObjectUtils.equals(value.getClass(), Identifier.class))
		{
			return Optional.of(((Identifier)value).ident);
		}
		else
			return Optional.absent();
	}
	
	public static Optional<Integer> getInt(Value value)
	{
		if(ObjectUtils.equals(value.getClass(), com.github.s262316.forx.tree.style.NumericValue.class))
		{
			return Optional.of((int)((com.github.s262316.forx.tree.style.NumericValue)value).amount);
		}
		else
			return Optional.absent();
	}	
	
	/**
	 * returns a ValueList. If value is not a ValueList,
	 * wraps it in a ValueList. otherwise returns value
	 * 
	 * @param value
	 * @return
	 */
	public static ValueList asValueList(Value value)
	{
		if(ObjectUtils.equals(value.getClass(), ValueList.class))
			return (ValueList)value;
		else
		{
			ValueList vl=new ValueList();
			vl.members.add(value);
			return vl;
		}
	}

	public static ValueList newValueList(Identifier... identifiers)
	{
		ValueList valuelist=new ValueList();
		
		valuelist.members=Lists.<Value>newArrayList(identifiers);
		
		return valuelist;
	}
	
	public static ValueList newValueList(NumericValue... nums)
	{
		ValueList valuelist=new ValueList();
		
		valuelist.members=Lists.<Value>newArrayList(nums);
		
		return valuelist;
	}	
	
	public static ValueList newValueList(StringValue... sv)
	{
		ValueList valuelist=new ValueList();
		
		valuelist.members=Lists.<Value>newArrayList(sv);
		
		return valuelist;
	}	
}


