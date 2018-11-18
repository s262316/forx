/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.s262316.forx.newbox;

import com.google.common.base.MoreObjects;

public class Dimensionable
{
    public static final int INVALID=Integer.MAX_VALUE;
    private int left, top;
    private int width, height;

    public Dimensionable()
    {
        left =INVALID;
        top =INVALID;
        width =INVALID;
        height =INVALID;
    }

    public Dimensionable(int left, int top, int width, int height)
    {
        this.left =left;
        this.top =top;
        this.width =width;
        this.height =height;
    }

    public void setPosition(int left, int top)
    {
        this.left =left;
        this.top =top;
    }

    public void setWidth(int width)
    {
        this.width =width;
    }

    public void setHeight(int height)
    {
        this.height =height;
    }

    public int left()
    {
        return left;
    }

    public int top()
    {
        return top;
    }

    public int right()
    {
        if(left ==INVALID|| width ==INVALID)
            return INVALID;

        if(width ==0)
            return left;

        return left + width -1;
    }

    public int bottom()
    {
        if(top ==INVALID|| height ==INVALID)
            return INVALID;

        if(height ==0)
            return top;

        return top + height -1;
    }

    public int width()
    {
        return width;
    }

    public int height()
    {
        return height;
    }

    public void changeHeight(int difference)
    {
        height +=difference;
    }

    public void changeWidth(int difference)
    {
        width +=difference;
    }

    public void shiftFromTop(int by)
    {
        top +=by;
    }

    public void shiftFromLeft(int by)
    {
        left +=by;
    }

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
		       .add("left", left)
		       .add("top", top)
		       .add("width", width)
		       .add("height", height)
		       .toString();
	}
}

