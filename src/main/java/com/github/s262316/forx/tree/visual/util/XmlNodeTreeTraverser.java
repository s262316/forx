package com.github.s262316.forx.tree.visual.util;

import java.util.Collections;
import java.util.List;

import com.github.s262316.forx.tree.impl.XmlDocument;
import com.github.s262316.forx.tree.impl.XmlElement;
import com.github.s262316.forx.tree.impl.XmlNode;

import com.google.common.collect.Lists;
import com.google.common.collect.TreeTraverser;

public class XmlNodeTreeTraverser extends TreeTraverser<XmlNode>
{
	@Override
	public Iterable<XmlNode> children(XmlNode node)
	{
		if(node instanceof XmlDocument)
		{
			return Lists.newArrayList((XmlNode)((XmlDocument)node).getRoot());
		}
		else if(node instanceof XmlElement)
		{
			return (List)((XmlElement)node).getMembers();
		}
		else
			return Collections.emptyList();
	}

}
