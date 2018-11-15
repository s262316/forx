package com.github.s262316.forx.newbox.relayouter;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import com.github.s262316.forx.newbox.Box;
import com.github.s262316.forx.newbox.relayouter.util.LayableIdOrdering;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class LayoutContext
{
	private static final Ordering<Box> DIRTY_ORDER=new LayableIdOrdering();
	
	private TreeSet<Box> dirtyList=Sets.newTreeSet(DIRTY_ORDER);

	public void addToDirty(List<Box> affected)
	{
		dirtyList.addAll(affected);
	}
	
	public void addToDirty(Box affected)
	{
		dirtyList.add(affected);
	}
	
	public void notDirty(Box lay)
	{
		Preconditions.checkArgument(dirtyList.contains(lay));
		
		dirtyList.remove(lay);
	}
	
	public Collection<Box> getDirties()
	{
		return ImmutableList.copyOf(dirtyList);
	}
}

