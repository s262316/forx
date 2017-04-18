package com.github.s262316.forx.style.selectors.util;

import javax.annotation.Nullable;

import com.github.s262316.forx.tree.XElement;
import com.github.s262316.forx.tree.XmlNode;
import com.github.s262316.forx.style.selectors.PseudoElementType;
import com.github.s262316.forx.style.selectors.Selector;

import com.google.common.base.Predicate;

public class SelectorPredicate implements Predicate<XmlNode>
{
	private Selector selector;
	
	public SelectorPredicate(Selector selector)
	{
		this.selector=selector;
	}
	
	@Override
	public boolean apply(@Nullable XmlNode input)
	{
		// TODO only works on elements not text
		if(input instanceof XElement)
			return selector.isMatch((XElement)input, PseudoElementType.PE_NOT_PSEUDO);
		else
			return false;
	}
}
