/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.style;

public class StringValue extends Value
{
    public String str;

    public StringValue()
    {
        super(false);
        str="";
    }

    public StringValue(String v)
    {
        super(true);
        str=v;
    }

    @Override
    public boolean equals(Object v)
    {
	boolean e=false;
	StringValue sv=(StringValue)v;

	if(sv.str.equals(str))
            e=true;

	return e;
    }

    @Override
    public String toString()
    {
	return "stringvalue : " + str + "\n";
    }


}
