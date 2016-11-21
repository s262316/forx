package com.github.s262316.forx.tree.style;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.base.Objects;

public class ValueList extends Value
{
    public ArrayList<Value> members=new ArrayList<Value>();

    public ValueList()
    {
        super(true);
    }

    public ValueList extract(int start, int end)
    {
	ValueList vl=new ValueList();
	int i;

	for(i=start; i<=end; i++)
            vl.members.add(members.get(i));

	return vl;
    }

    public int size()
    {
	return members.size();
    }

    public Value get(int i)
    {
        return members.get(i);
    }

    @Override
    public boolean equals(Object v)
    {
            boolean e=true;
            ValueList vl=(ValueList)v;

            Iterator<Value> it1=members.iterator();
            Iterator<Value> it2=members.iterator();
            while(it1.hasNext() && it2.hasNext() && e)
            {
                e=it1.next().equals(it2.next());
            }

            return e;
    }


    @Override
    public String toString()
    {
		return Objects.toStringHelper(this)
				.add("members", members)
				.toString();
    }    
    
}
