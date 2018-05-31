package com.github.s262316.forx.css;

import java.util.List;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.github.s262316.forx.tree.NodeType;
import com.github.s262316.forx.tree.XElement;
import com.github.s262316.forx.tree.XNode;
import com.github.s262316.forx.tree.XmlNode;
import com.github.s262316.forx.tree.util.IsFirstInParent;

public class StyleXNodes
{
	/**
	 * same as XNode.prev except returns first node that is an element.
	 * 
	 * @return
	 */
	public static XNode previous(XNode node)
	{
		XNode prev;
		
		prev=node.previousNode();
		while(prev!=null && prev.type()!=NodeType.X_ELEMENT)
		{
			prev=prev.previousNode();
		}
		
		return prev;
	}
	
	public static XNode firstMember(XNode node)
	{
		if(node instanceof XElement)
		{
			XElement xe=(XElement)node;
			return Iterables.find(xe.getMembers(), Predicates.instanceOf(XElement.class), null);
		}
		
		return null;
	}
	
	public static boolean isFirstForContent(List<XmlNode> nodes)
	{
		return Iterables.all(nodes, new IsFirstInParent());
	}
}


