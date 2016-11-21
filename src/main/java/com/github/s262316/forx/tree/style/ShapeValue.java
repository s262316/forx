/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.tree.style;

// the processor does not produce this type
public class ShapeValue extends Value
{
    NumericValue top, right, bottom, left;

    public ShapeValue(NumericValue t, NumericValue r, NumericValue b, NumericValue l)
    {
        super(true);
        top=t;
        right=r;
        bottom=b;
        left=l;
    }

    @Override
    public String toString()
    {
	return "shapevalue : " + top + ", " + right + ", " + bottom + ", " + left + "\n";

    }

    @Override
    public boolean equals(Object v)
    {
	boolean e=false;
	ShapeValue sv=(ShapeValue)v;

	if(sv.bottom.equals(bottom) && sv.left.equals(left) && sv.right.equals(right) && sv.top.equals(top))
            e=true;

	return e;
    }
}
