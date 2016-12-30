package com.github.s262316.forx.box.relayouter;

import com.github.s262316.forx.box.Layable;

import com.google.common.collect.TreeTraverser;

public class LayableTreeTraverser extends TreeTraverser<Layable>
{
	@Override
	public Iterable<Layable> children(Layable layable)
	{
		return layable.getMembersAll();
	}

}
