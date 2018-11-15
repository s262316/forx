package com.github.s262316.forx.box;

import com.github.s262316.forx.box.util.SizeResult;

public interface Rowular
{
	public SizeResult compute_dimensions();
	public void row_back(TableRow c);
	public Rowular container();
	public void set_min_height(int minh);
	public int min_height();
	public void change_min_height(int minh);
}


