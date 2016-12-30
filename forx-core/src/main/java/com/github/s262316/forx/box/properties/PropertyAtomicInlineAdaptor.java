package com.github.s262316.forx.box.properties;

import java.awt.Font;

import com.github.s262316.forx.box.AtomicInline;

public class PropertyAtomicInlineAdaptor implements PropertyAdaptor
{
	private AtomicInline inline;
	
	public PropertyAtomicInlineAdaptor(AtomicInline inline)
	{
		this.inline=inline;
	}

	@Override
	public int contentWidth()
	{
		return inline.content_width();
	}

	@Override
	public int contentHeight()
	{
		return inline.content_height();
	}

	@Override
	public PropertyAdaptor getContainer()
	{
		return new PropertyBoxAdaptor(inline.getContainer());
	}

	@Override
	public Font getFont()
	{
		return inline.getFont();
	}
}

