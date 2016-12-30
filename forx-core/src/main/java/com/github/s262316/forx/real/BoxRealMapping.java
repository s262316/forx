package com.github.s262316.forx.real;

import java.util.List;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Inline;
import com.github.s262316.forx.tree.impl.XmlNode;
import com.github.s262316.forx.tree.visual.XmlVElement;

public interface BoxRealMapping
{
	public void update(Inline t, int x, int y, int width, int height);
	public void update(Box box, int x, int y, int width, int height);
	public List<XmlNode> componentsAtPoint(int x, int y);
	public void add(XmlVElement element, Box visualBox);
	public void invalidateRealPositions();
}
