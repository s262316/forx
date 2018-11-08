package com.github.s262316.forx.box.mediator;

import java.util.List;

import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.tree.visual.VElement;

import com.github.s262316.forx.box.Box;

public interface BoxRelation
{
	void add(InlineBox child);

	public List<Box> routeToRoot(Box low);

	boolean isDummyInlineContainer();
}
