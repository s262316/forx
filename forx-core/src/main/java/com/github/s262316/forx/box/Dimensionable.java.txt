/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.s262316.forx.box;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Dimensionable
{
    public static final int INVALID=Integer.MAX_VALUE;
    private int _left, _top;
    private int _width, _height;

    public Dimensionable()
    {
        _left=INVALID;
        _top=INVALID;
        _width=INVALID;
        _height=INVALID;
    }

    public Dimensionable(int left, int top, int width, int height)
    {
        _left=left;
        _top=top;
        _width=width;
        _height=height;
    }

    public void set_position(int left, int top)
    {
        _left=left;
        _top=top;
    }

    public void set_width(int width)
    {
        _width=width;
    }

    public void set_height(int height)
    {
        _height=height;
    }

    public int left()
    {
        return _left;
    }

    public int top()
    {
        return _top;
    }

    public int right()
    {
        if(_left==INVALID||_width==INVALID)
            return INVALID;

        if(_width==0)
            return _left;

        return _left+_width-1;
    }

    public int bottom()
    {
        if(_top==INVALID||_height==INVALID)
            return INVALID;

        if(_height==0)
            return _top;

        return _top+_height-1;
    }

    public int width()
    {
        return _width;
    }

    public int height()
    {
        return _height;
    }

    public void change_height(int difference)
    {
        _height+=difference;
    }

    public void change_width(int difference)
    {
        _width+=difference;
    }

    public void shift_from_top(int by)
    {
        _top+=by;
    }

    public void shift_from_left(int by)
    {
        _left+=by;
    }

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
		       .add("left", _left)
		       .add("top", _top)
		       .add("width", _width)
		       .add("height", _height)
		       .toString();
	}
}

