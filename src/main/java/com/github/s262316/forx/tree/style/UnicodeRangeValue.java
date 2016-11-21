/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.tree.style;

public class UnicodeRangeValue extends Value
{
    char from, to;

    public UnicodeRangeValue()
    {
        super(false);
        from=0;
        to=0;
    }

    public UnicodeRangeValue(char f)
    {
        super(false);
        from=f;
        to=0;
    }

    public UnicodeRangeValue(char f, char t)
    {
        super(false);
        from=f;
        to=t;
    }

    @Override
    public String toString()
    {
	return "UnicodeRangeValue :" + from + " - " + to + "\n";
    }

    @Override
    public boolean equals(Object v)
    {
	boolean e=false;
	UnicodeRangeValue urv=(UnicodeRangeValue)v;

	if(urv.from==from && urv.to==to)
            e=true;

	return e;
    }

}
