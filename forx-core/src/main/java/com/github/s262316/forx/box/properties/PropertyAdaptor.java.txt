package com.github.s262316.forx.box.properties;

import java.awt.Font;

public interface PropertyAdaptor
{
	public int contentWidth();
	public int contentHeight();
	public PropertyAdaptor getContainer();
	public Font getFont();
}

