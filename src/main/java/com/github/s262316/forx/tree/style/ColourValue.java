/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.tree.style;

// the processor does not produce this type
public class ColourValue extends Value
{
    public int r, g, b;

    public ColourValue()
    {
        super(false);
        r=g=b=0;
    }

    public ColourValue(int _r, int _g, int _b)
    {
        super(true);
        r=_r;
        g=_g;
        b=_b;
    }

    @Override
    public String toString()
    {
        return "colourvalue : " + r + " " + g + " " + b + "\n";
    }

    @Override
    public boolean equals(Object v)
    {
	boolean e=false;
	ColourValue cv=(ColourValue)v;

	if(cv.b==b && cv.g==g && cv.r==r)
            e=true;

	return e;
    }

}
