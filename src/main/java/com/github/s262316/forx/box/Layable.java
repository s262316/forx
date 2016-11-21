/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.s262316.forx.box;

import java.util.List;

import com.github.s262316.forx.box.relayouter.LayoutResult;
import com.github.s262316.forx.box.util.SizeResult;

public interface Layable
{
// life cycle methods called in this order
// container class calls first 4
    public SizeResult compute_dimensions(); // don't implement this here
    public LayoutResult calculate_position(Layable member); // or this
    public void uncalculate_position(Layable member); // or this
    public void computeProperties();

    public void set_dimensions(int width, int height);

    public void set_position(int left, int top);
    public void unsetPosition();

    public void set_width(int width);

    public void set_height(int height);

    public int preferred_width();

    public int preferred_min_width();

    public int preferred_shrink_width(int avail_width);

    public void set_container(Box cont);

    @Deprecated
    public Box container();
    public Box getContainer();

    public TableBox table_root();

    public int getRelTop();

    public int getRelLeft();

    public boolean is_relative();

    public void set_relative(boolean to);

    public int left();

    public int top();

    public int right();

    public int bottom();

    public int width();

    public int height();
	@Deprecated
    public void change_height(int difference);
	@Deprecated
    public void change_width(int difference);

    public List<Layable> getMembersAll();

    public List<Box> getMembersFlowing();

    public List<FloatBox> getMembersFloating();

    public List<AbsoluteBox> getMembersPositioned();

	public boolean flows();

	public int getId();
	
	public void setId(int id);

	@Deprecated
    public int stretchAmountWidth(Layable whichChild, int maxExtraAmount);
	@Deprecated
    public int stretchAmountHeight(Layable whichChild, int y, int maxExtraAmount);

	/** may only be called on auto widths */
	public void setFutureWidth(int futureWidth);
	/** may only be called on auto heights */
	public void setFutureHeight(int futureHeight);    
}
