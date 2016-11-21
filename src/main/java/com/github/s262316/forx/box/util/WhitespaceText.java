package com.github.s262316.forx.box.util;

import org.apache.commons.lang3.StringUtils;

import com.github.s262316.forx.tree.impl.XmlNode;
import com.github.s262316.forx.tree.visual.XmlVText;

import com.google.common.base.Predicate;

public class WhitespaceText implements Predicate<XmlNode>
{
	@Override
	public boolean apply(XmlNode node)
	{
		if(!node.getClass().equals(XmlVText.class))
			return false;
		
		XmlVText textNode=(XmlVText)node;
		
		return StringUtils.isWhitespace(textNode.getValue());
	}
}

