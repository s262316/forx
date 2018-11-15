package com.github.s262316.forx.box.mediator;

import java.util.List;

import com.github.s262316.forx.box.Box;

public interface BoxRelation
{
	public List<Box> routeToRoot(Box low);
}
