package com.github.s262316.forx.box.util;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class SizeResult
{
	public Length width, height;
	public Length marginLeft, marginRight;
	public Length marginTop, marginBottom;
	public boolean auto_width, auto_height;
	public Length min_width, max_width;
	public Length min_height, max_height;

	public SizeResult()
	{
		width=new Length(0);
		height=new Length(0);
		marginLeft=new Length(0);
		marginRight=new Length(0);
		marginTop=new Length(0);
		marginBottom=new Length(0);
		auto_width=false;
		auto_height=false;
		min_width=new Length(0);
		max_width=new Length(0);
		min_height=new Length(0);
		max_height=new Length(0);
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
			       .add("width",width)
			       .add("height", height)
			       .toString();
	}
	
	
}
