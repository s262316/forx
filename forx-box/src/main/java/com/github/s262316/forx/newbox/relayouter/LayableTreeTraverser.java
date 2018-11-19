package com.github.s262316.forx.newbox.relayouter;

import com.github.s262316.forx.newbox.Box;
import com.google.common.collect.TreeTraverser;

public class LayableTreeTraverser extends TreeTraverser<Box>
{
	@Override
	public Iterable<Box> children(Box layable)
	{
		return layable.getInterBoxOps().getMembers();
	}
}
