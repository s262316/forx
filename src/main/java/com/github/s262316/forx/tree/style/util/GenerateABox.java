package com.github.s262316.forx.tree.style.util;

import java.util.List;

import javax.annotation.Nullable;

import com.github.s262316.forx.tree.XNodes;
import com.github.s262316.forx.tree.impl.XmlNode;

import com.google.common.base.Predicate;

public class GenerateABox implements Predicate<XmlNode>
{
	@Override
	public boolean apply(@Nullable XmlNode input)
	{
		List<XmlNode> pathToHere=XNodes.pathToHere(input);
		
		for(XmlNode n : pathToHere)
		{
			if(n.getName().equals("head"))
				return false;
		}
		
		return true;
	}
}
