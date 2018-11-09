package com.github.s262316.forx.box.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.BoxCounter;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.RootBox;
import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.relayouter.LayableTreeTraverser;
import com.github.s262316.forx.tree.visual.AnonReason;
import com.github.s262316.forx.util.ExtractBoxId;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

public class Boxes
{
	public static Box absoluteContainer(Box abs)
	{
        Box ancestor=abs;
        while(BoxTypes.isAbsoluteBox(ancestor) == false && ancestor.is_relative() == false && ancestor.container() != null)
            ancestor=ancestor.container();
        
        return ancestor;
	}
	
	public static boolean existsInSpace(Layable box)
	{
		return box.height()>0 && box.width()>0;
	}
	
	public static Box root(Layable from)
	{
		if(from instanceof RootBox)
			return (Box)from;

		return root(from.container());
	}
	
	public static void renumber(Box root)
	{
		LayableTreeTraverser traverser=new LayableTreeTraverser();
		FluentIterable<Layable> fit=traverser.preOrderTraversal(root);
		
		BoxCounter.count=0;
		
		Iterator<Layable> lit=fit.iterator();
		while(lit.hasNext())
		{
			Layable l=lit.next();
			
			l.setId(BoxCounter.count++);
		}
	}

	public static List<Integer> allIds(Box start)
	{
		List<Integer> all=new LayableTreeTraverser()
			.preOrderTraversal(start)
			.transform(new ExtractBoxId())
			.toList();

		return all;
	}
	
	public static boolean isInside(Layable outer, Layable inner)
	{
		Box container;
		
		container=inner.container();
		while(container.container()!=null)
		{
			if(container==outer)
				return true;
			
			container=container.container();
		}
		
		return false;
	}
	
	public static List<Box> routeToRoot(Box low)
	{
		List<Box> route=new ArrayList<>();
		Box b;
		
		b=low;
		do
		{
			route.add(b);
			b=b.container();
		}		
		while(b.container()!=null);
		
		return route;
	}

	public static Optional<InlineBox> getLastFlowMemberAnAnonInlineContainer(Box box)
	{
		List<Box> flowing=box.getMembersFlowing();
		Box lastFlow;

		if (!flowing.isEmpty())
		{
			lastFlow=Iterables.getLast(flowing);
			
			if(BoxTypes.isInlineBox(lastFlow) &&
					lastFlow.getVisual().getAnonReason()==AnonReason.INLINE_CONTAINER)
			{
				return Optional.of((InlineBox)lastFlow);
			}
		}
		
		return Optional.empty();
	}
}

