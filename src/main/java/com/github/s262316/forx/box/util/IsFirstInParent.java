package com.github.s262316.forx.box.util;

import java.util.List;

import javax.annotation.Nullable;

import com.github.s262316.forx.tree.impl.XmlElement;
import com.github.s262316.forx.tree.impl.XmlNode;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

public class IsFirstInParent implements Predicate<XmlNode>
{
	@Override
	@Nullable
	public boolean apply(@Nullable XmlNode input)
	{
		if(!(input instanceof XmlElement))
			return true;

		XmlElement element=(XmlElement)input;
		XmlElement container=(XmlElement)element.parentNode();
		
		List<XmlNode> allMembers=(List)container.getMembers();
		Iterable<XmlNode> notWhitespaceMembers=Iterables.filter(allMembers, Predicates.not(new WhitespaceText()));
		
		XmlNode first=Iterables.getFirst(notWhitespaceMembers, null);
		if(first==input)
			return true;
		else
			return false;
	}
}
