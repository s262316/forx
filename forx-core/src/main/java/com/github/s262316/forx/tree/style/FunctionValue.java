/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.tree.style;

public class FunctionValue extends Value
{
    public String name;
    public ValueList values;

    public FunctionValue()
    {
        super(false);
        name="";
        values=new ValueList();
    }

    public FunctionValue(String n)
    {
        super(true);
        name=n;
        values=new ValueList();
    }

    public FunctionValue(String n, ValueList args)
    {
        super(true);
        name=n;
        values=args;
    }    
    
    public String print()
    {
        String str="";

	str+= "function name: " + name + "\n";

	for(Value v : values.members)
	{
           str+= "arg : " + v.toString();
	}

        return str;
    }

    @Override
    public boolean equals(Object v)
    {
	boolean e=false;
	FunctionValue fv=(FunctionValue)v;

	if(fv.name.equals(name) && values.equals(fv.values))
		e=true;

	return e;
    }


}
