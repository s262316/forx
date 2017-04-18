package com.github.s262316.forx.style;

import java.util.EnumSet;
import java.util.Map;

import com.github.s262316.forx.style.selectors.Selector;

import com.google.common.base.Objects;

public class StyleRule
{
    private Selector selector;
    public Map<String, Declaration> declarations;
    private EnumSet<MediaType> media;
    private int order;

    public StyleRule(Selector selector, Map<String, Declaration> declarations, EnumSet<MediaType> media, int order)
	{
		this.selector = selector;
		this.declarations = declarations;
		this.media = media;
		this.order = order;
	}
    
    public Selector getSelector()
	{
		return selector;
	}
    
	public Map<String, Declaration> getDeclarations()
	{
		return declarations;
	}
	
	public EnumSet<MediaType> getMedia()
	{
		return media;
	}

	public int getOrder()
	{
		return order;
	}

	@Override
    public String toString()
    {
		return Objects.toStringHelper(this)
				.add("selector", selector)
				.add("declarations", declarations)
				.add("media", media)
				.add("order", order)
				.toString();

    }
}
