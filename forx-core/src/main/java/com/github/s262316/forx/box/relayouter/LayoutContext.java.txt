package com.github.s262316.forx.box.relayouter;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.relayouter.util.LayableIdOrdering;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class LayoutContext
{
	private static final Ordering<Layable> DIRTY_ORDER=new LayableIdOrdering();
	
	private TreeSet<Layable> dirtyList=Sets.newTreeSet(DIRTY_ORDER);

	public void addToDirty(List<Layable> affected)
	{
		dirtyList.addAll(affected);
	}
	
	public void addToDirty(Layable affected)
	{
		dirtyList.add(affected);
	}
	
	public void notDirty(Layable lay)
	{
		Preconditions.checkArgument(dirtyList.contains(lay));
		
		dirtyList.remove(lay);
	}
	
	public Collection<Layable> getDirties()
	{
		return ImmutableList.copyOf(dirtyList);
	}
}

