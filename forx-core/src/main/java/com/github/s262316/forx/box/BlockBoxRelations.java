package com.github.s262316.forx.box;

import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.mediator.BoxRelation;
import com.github.s262316.forx.box.relayouter.util.LayoutUtils;
import com.github.s262316.forx.box.util.Boxes;
import com.github.s262316.forx.tree.visual.VElement;
import com.github.s262316.forx.tree.visual.XmlVElement;
import com.google.common.collect.Iterables;

import java.util.List;

public class BlockBoxRelations implements BoxRelation
{
	private VElement parentVElement;
	private BlockBox blockBox;

	public BlockBoxRelations(VElement parentVElement, BlockBox blockBox)
	{
		this.parentVElement = parentVElement;
		this.blockBox = blockBox;
	}

	@Override
	public boolean isDummyInlineContainer()
	{
		return false;
	}

	@Override
	public void add(InlineBox child)
	{
		Box dummy;

		// is the last box an anon of type DUMMY_INLINE_CONTAINER?
		dummy=Iterables.getLast(blockBox.getMembersFlowing(), null);

		if(dummy==null || !dummy.getRelations().isDummyInlineContainer())
		{
			dummy = parentVElement.createAnonInlineBox();
			blockBox.flow_back(dummy);
		}

		dummy.flow_back((Box)child);
	}

	@Override
	public List<Box> routeToRoot(Box low)
	{
		return null;
	}
}
