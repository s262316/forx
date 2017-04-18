package com.github.s262316.forx.tree.visual;

import com.github.s262316.forx.tree.XmlText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.graphics.GraphicsContext;

public class XmlVText extends XmlText
{
	private final static Logger logger=LoggerFactory.getLogger(XmlVText.class);

	private GraphicsContext graphicsContext;

	public XmlVText(String t, XmlVDocument doc, int id, GraphicsContext gfxCtx)
	{
		super(t, doc, id);

		graphicsContext=gfxCtx;
	}

	public void self_pollenate()
	{
		Box container_box;

		logger.debug("self_pollenate");

		container_box=visualParentNode().visualBox();
		// check that the container doesn't have a display of none..
		if(container_box!=null)
		{
			visualParentNode().parse_and_add_text(getValue(), container_box);
		}
		else
			logger.debug("container_box is null");

	}

	public void self_depollenate()
	{

	}

	public XmlVElement visualParentNode()
	{
		return (XmlVElement)parentNode();
	}
}

