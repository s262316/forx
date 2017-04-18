package com.github.s262316.forx.box.util;

import com.github.s262316.forx.tree.XmlText;
import org.apache.commons.lang3.StringUtils;

import com.github.s262316.forx.tree.XmlNode;

import com.google.common.base.Predicate;

public class WhitespaceText implements Predicate<XmlNode>
{
	@Override
	public boolean apply(XmlNode node)
	{
		if(!node.getClass().equals(XmlText.class))
			return false;
		
		XmlText textNode=(XmlText)node;
		
		return StringUtils.isWhitespace(textNode.getValue());
	}
}

