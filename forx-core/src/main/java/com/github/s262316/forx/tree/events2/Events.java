package com.github.s262316.forx.tree.events2;

import java.util.LinkedHashSet;
import java.util.List;

import com.github.s262316.forx.tree.XNode;
import com.github.s262316.forx.tree.XNodes;
import com.github.s262316.forx.tree.impl.XmlNode;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class Events
{
	public static LinkedHashSet<XmlNode> findEventPath(XmlNode node)
	{
		return Sets.newLinkedHashSet(XNodes.pathToHere(node));
	}
	
	public static List<LinkedHashSet<XmlNode>> findEventPaths(List<XmlNode> nodesHit)
	{
		List<LinkedHashSet<XmlNode>> uniqueEventPaths=Lists.newArrayList();
		
		for(XmlNode hitNode : nodesHit)
		{
			List<XmlNode> hitPath=XNodes.pathToHere(hitNode);
			LinkedHashSet<XmlNode> p=Sets.newLinkedHashSet(hitPath);

			LinkedHashSet<XmlNode> bestPath=findBestPath(uniqueEventPaths, p);
			if(bestPath!=null)
				bestPath.addAll(p);
		}
		
		return uniqueEventPaths;		
	}
	
	private static LinkedHashSet<XmlNode> findBestPath(List<LinkedHashSet<XmlNode>> uniqueEventPaths, LinkedHashSet<XmlNode> pathToHere)
	{
		LinkedHashSet<XmlNode> best=null;
		int bestCount=0;
		
		for(LinkedHashSet<XmlNode> eventPath : uniqueEventPaths)
		{
			if(eventPath.containsAll(pathToHere))
			{
				// the uniqueEventPath is A,B,C,D and p is A,B,C
				// do nothing
				// in fact stop this now.
				return null;
			}
			else if(pathToHere.containsAll(eventPath))
			{
				// the uniqueEventPath is A,B,C and p is A,B,C,D
				// add D to the uniqueEventPath
//				eventPath.addAll(pathToHere);
				
				SetView<XmlNode> same=Sets.intersection(eventPath, pathToHere);
				if(same.contains(Iterables.get(eventPath, 0)) &&
						same.contains(Iterables.get(pathToHere, 0)))
				{
					if(same.size() > bestCount)
						best=eventPath;
				}
			}
		}
		
		if(best==null)
		{
			best=new LinkedHashSet<>();
			uniqueEventPaths.add(best);
		}
		
		return best;
	}
}
