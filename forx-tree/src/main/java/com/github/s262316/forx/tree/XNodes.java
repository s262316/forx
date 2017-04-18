package com.github.s262316.forx.tree;

import java.util.LinkedList;
import java.util.List;

import com.github.s262316.forx.tree.XmlElement;
import com.github.s262316.forx.tree.XmlNode;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class XNodes
{
	public static List<XmlNode> parentToChildPath(XmlNode top, XmlNode bottom)
	{
		LinkedList<XmlNode> path=Lists.newLinkedList();
		XNode parent=bottom;
		
		while(parent!=top)
		{
			path.addFirst((XmlNode)parent);
			parent=parent.parentNode();
		}
		
		return path;
	}

	/**
	 * 
	 * @param target
	 * @return top is first in the returned list. i.e. document
	 */
	public static List<XmlNode> pathToHere(XmlNode target)
	{
		LinkedList<XmlNode> path=Lists.newLinkedList();
		XNode parent=target;

		while(parent!=null)
		{
			path.addFirst((XmlNode)parent);
			parent=parent.parentNode();
		}

		return path;
	}
	
	// TODO should this be recursive and what does it do with
	// non-text nodes????
	public static String collectAllText(XElement e)
	{
		String str="";

		for(XNode xn : e.getMembers())
		{
			str+=xn.getValue();
		}

		return str;
	}

	public static String getAttributeValue(XmlElement element, String attrName, String defaultValue)
	{
		return Optional.fromNullable(element.getAttr(attrName)).transform(XAttribute::getValue).or(defaultValue);
	}

	public static Optional<String> getAttributeValue(XmlElement element, String attrName)
	{
		return Optional.fromNullable(element.getAttr(attrName)).transform(XAttribute::getValue);
	}
}

