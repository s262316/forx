package com.github.s262316.forx.tree.visual;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import com.github.s262316.forx.css.CSSPropertiesReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.css.CSSParser;
import com.github.s262316.forx.css.CSSParserException;
import com.github.s262316.forx.tree.XAttribute;
import com.github.s262316.forx.tree.events2.MutationType;
import com.github.s262316.forx.tree.events2.PropagationType;
import com.github.s262316.forx.tree.events2.XMutationListener;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;
import com.github.s262316.forx.tree.impl.XmlDocument;
import com.github.s262316.forx.tree.style.Declaration;
import com.github.s262316.forx.tree.style.util.SelectorPredicate;
import com.github.s262316.forx.tree.style.util.Selectors;

/**
 * listens for elements with style attributes
 * 
 * if a style attribute is present, then
 * creates a stylesheet and merges into doc's stylesheet
 * 
 * PropagationType = ON_TARGET
 * mutationType=ADDED only
 * 
 *
 */
public class StyleAttributeHandler extends XMutationListener
{
	private final static Logger logger=LoggerFactory.getLogger(StyleAttributeHandler.class);
	
	/** element that has been added/removed/connected/disconnected */
	private XmlVElement changed;
	private CSSPropertiesReference cssPropertiesReference;
	
	public StyleAttributeHandler(XmlVElement changed, CSSPropertiesReference cssPropertiesReference)
	{
		super(new SelectorPredicate(Selectors.createAttributeNameSelector("style")),
				PropagationType.ON_TARGET, changed, EnumSet.of(MutationType.ADD));
		
		this.changed=changed;
		this.cssPropertiesReference=cssPropertiesReference;
	}
	
	@Override
	public void added(XmlMutationEvent event)
	{
		logger.debug("eventAdd event={} listenee={}", event, getListenee());

		// look on style attribute
		XAttribute a=changed.getAttr("style");
		if(a!=null)
			processElementStyles(a);
	}

	@Override
	public void removed(XmlMutationEvent event)
	{
	}

	@Override
	public void connected(XmlMutationEvent event)
	{
	}

	@Override
	public void disconnected(XmlMutationEvent event)
	{
	}
	
	private void processElementStyles(XAttribute a)
	{
		logger.debug("processElementStyles");

		try
		{
			CSSParser parser=new CSSParser(a.getValue(), (XmlDocument)changed.getDocument(), ((XmlDocument)changed.getDocument()).getCssLoader(), cssPropertiesReference);
			List<Declaration> decs=parser.parse_declist();

			changed.setStyles(decs);
		}
		catch(CSSParserException cpe)
		{}
		catch(IOException ioe)
		{
		}
	}	
}
