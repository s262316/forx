package com.github.s262316.forx.tree.visual;

import java.util.EnumSet;
import java.util.Map;

import com.github.s262316.forx.css.VisualConstants;
import com.github.s262316.forx.tree.events2.MutationType;
import com.github.s262316.forx.tree.events2.PropagationType;
import com.github.s262316.forx.tree.events2.XMutationListener;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;
import com.github.s262316.forx.style.selectors.util.SelectorPredicate;
import com.github.s262316.forx.style.selectors.util.Selectors;

public class MetaTagHandler extends XMutationListener
{
	private XmlVElement metaElement;
	
	public MetaTagHandler(XmlVElement metaElement)
	{
		super(new SelectorPredicate(Selectors.createSimpleElementNameSelector("meta")),
				PropagationType.ON_TARGET, metaElement, EnumSet.of(MutationType.ADD));
		
		this.metaElement=metaElement;
	}
	
	@Override
	public void added(XmlMutationEvent event)
	{
		if(metaElement.getAttributes().containsKey("name") && metaElement.getAttributes().containsKey("content"))
		{
			String name=metaElement.getAttr("name").getValue();
			String content=metaElement.getAttr("content").getValue();
			
			event.getSubject().getDocument().getProperty(VisualConstants.META_TAGS, Map.class).put(name, content);
		}
	}

	@Override
	public void disconnected(XmlMutationEvent event)
	{
	}

	@Override
	public void connected(XmlMutationEvent event)
	{
	}

	@Override
	public void removed(XmlMutationEvent event)
	{
	}
}
