package com.github.s262316.forx.box;

import com.github.s262316.forx.box.util.SizeResult;

public interface Columnular
{
	public SizeResult compute_dimensions();
	public int min_width();
	public void set_min_width(int mw);
	public void change_min_width(int amount);
	public void col_back(Column c);
};

