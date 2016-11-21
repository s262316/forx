package com.github.s262316.forx.tree.visual;

import java.io.IOException;
import java.util.EnumSet;

import com.google.common.base.Preconditions;

import com.github.s262316.forx.css.CSSParser;
import com.github.s262316.forx.css.CSSParserException;
import com.github.s262316.forx.tree.XNodes;
import com.github.s262316.forx.tree.events2.MutationType;
import com.github.s262316.forx.tree.events2.PropagationType;
import com.github.s262316.forx.tree.events2.XMutationListener;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;
import com.github.s262316.forx.tree.impl.XmlDocument;
import com.github.s262316.forx.tree.style.Stylesheet;
import com.github.s262316.forx.tree.style.util.SelectorPredicate;
import com.github.s262316.forx.tree.style.util.Selectors;

/**
 * listens for elements named style
 * 
 * PropagationType = CAPTURE
 * mutationType=ADDED,CONNECTED,DISCONNECTED
 * 
 * collect all contained text and create a stylesheet.
 * ADDED = parse the stylesheet. if added and ON_TARGET probably nothing in the stylesheet
 * CONNECTED = associated the stylesheet with the doc
 * DISCONNECTED = disassociate the stylesheet with the doc
 * REMOVED = nothing
 *  
 *
 */
public class StyleElementHandler extends XMutationListener
{
	private XmlVElement styleElement;
	private Stylesheet styleElementStylesheet;
	private boolean connected=false;
	
	public StyleElementHandler(XmlVElement styleElement)
	{
		super(new SelectorPredicate(Selectors.createSimpleElementNameSelector("style")),
				PropagationType.CAPTURE, styleElement, EnumSet.of(MutationType.ADD, MutationType.CONNECT, MutationType.DISCONNECT));
		
		this.styleElement=styleElement;
	}

	@Override
	public void added(XmlMutationEvent event)
	{
		try
		{
			String text=XNodes.collectAllText(styleElement);

			CSSParser parser=new CSSParser(text, (XmlDocument)styleElement.getDocument());
			styleElementStylesheet=parser.parse_stylesheet();

			if(connected)
			{
				((XmlDocument)styleElement.getDocument()).mergeStyles(styleElement, styleElementStylesheet);
			}
		}
		catch(CSSParserException cpe)
		{}
		catch(IOException ioe)
		{} // ?
	}
	
	@Override
	public void removed(XmlMutationEvent event)
	{
		String text=XNodes.collectAllText(styleElement);

		if(connected)
		{
			((XmlDocument)styleElement.getDocument()).demergeStylesFrom(styleElement);
		}
	}	
	
	@Override
	public void connected(XmlMutationEvent event)
	{
		if(event.getSubject()==styleElement && styleElementStylesheet!=null)
		{
			Preconditions.checkArgument(connected==false);
			
			((XmlDocument)styleElement.getDocument()).mergeStyles(styleElement, styleElementStylesheet);
			connected=true;
		}
	}
	
	@Override
	public void disconnected(XmlMutationEvent event)
	{
		// only demerge if style is disconnected
		if(event.getSubject()==styleElement)
		{
			Preconditions.checkArgument(connected==true);
			
			((XmlDocument)styleElement.getDocument()).demergeStylesFrom(styleElement);
			connected=false;
		}
	}

}
