package com.github.s262316.forx.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.common.ApplicationConfigException;
import com.github.s262316.forx.tree.build.AttributeKey;
import com.github.s262316.forx.tree.build.ElementKey;
import com.github.s262316.forx.tree.build.TextKey;
import com.github.s262316.forx.tree.build.XmlDocumentBuilder;
import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.tree.impl.XmlDocument;
import com.github.s262316.forx.tree.impl.XmlNode;
import com.github.s262316.forx.tree.visual.XmlVElement;


public class Html33Binding extends HTMLEditorKit.ParserCallback 
{
	private final static Logger logger=LoggerFactory.getLogger(Html33Binding.class);

    private XmlDocument doc;
    private XmlVElement root=null;
    private XmlVElement current=null;
    private XmlDocumentBuilder builder;

    public Html33Binding(EventDispatcher eventDispatcher, XmlDocumentBuilder builder) throws ApplicationConfigException
    {
        this.builder=builder;
        
	    doc=new XmlDocument(null, builder, eventDispatcher, null);
    }

    public XmlDocument getDoc()
    {
		return doc;
	}

    @Override
    public void flush()
    {
		logger.debug("flush");

    }

    @Override
    public void handleComment(char[] data, int pos)
    {
		logger.debug("handleComment "+new String(data)+" "+pos);

    }

    @Override
    public void handleEndOfLineString(String eol)
    {
		logger.debug("handleEndOfLineString");

    }

    @Override
    public void handleEndTag(HTML.Tag t, int pos)
    {
    	logger.debug("handleEndTag current={}"+t.toString()+" "+pos, current==null?"null":current.getName());

		current.complete(false);
		
		current=current.visualParentNode();
    }

    @Override
    public void handleError(String errorMsg, int pos)
    {
		logger.debug("handleError "+errorMsg+" "+pos);
    }

    @Override
    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos)
    {
		try
		{
			logger.debug("handleSimpleTag parent={} "+t.toString()+" "+pos, current==null?"null":current.getName());

			Object obj;
			ElementKey ek=new ElementKey();
			ek.name=t.toString();
			ek.members=new ArrayList<XmlNode>();
			ek.attrs=new HashMap<String, AttributeKey>();
			ek.whitespaceIsSignificant=false;
			ek.lang="en-GB";

			Enumeration es=a.getAttributeNames();
			String attrName, value;
			AttributeKey ak;
			HTML.Attribute attr;

			while(es.hasMoreElements())
			{
				obj=es.nextElement();
				if(obj instanceof HTML.Attribute)
				{
					attr=(HTML.Attribute)obj;
					logger.debug(attr.toString());

					ak=new AttributeKey();
					ak.name=attr.toString();
					ak.value=(String)a.getAttribute(attr);

					ek.attrs.put(attr.toString(), ak);
				}								
				else
					logger.debug("ignored attribute "+obj);
			}
			
			XmlVElement ve=(XmlVElement)builder.createElement(ek);

			if(root==null)
			{
				root=ve;
				doc.add(ve);
			}
			else
				current.add(ve);

			current=ve;
		}
		catch(Exception e)
		{
			e.printStackTrace();

			throw new RuntimeException(e);
		}
    }

    @Override
    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos)
    {
		try
		{
			logger.debug("handleStartTag current={} "+t.toString()+" "+pos, current==null?"null":current.getName() );

			ElementKey ek=new ElementKey();
			ek.name=t.toString();
			ek.members=new ArrayList<XmlNode>();
			ek.attrs=new HashMap<String, AttributeKey>();
			ek.whitespaceIsSignificant=false;
			ek.lang="en-GB";

			Enumeration es=a.getAttributeNames();
			AttributeKey ak;
			HTML.Attribute attr;
			Object obj;

			while(es.hasMoreElements())
			{
				obj=es.nextElement();

//				if(!attr.toString().equals(HTMLEditorKit.ParserCallback.IMPLIED))
				if(obj instanceof HTML.Attribute)
				{
					attr=(HTML.Attribute)obj;
					logger.debug(attr.toString());

					ak=new AttributeKey();
					ak.name=attr.toString();
					ak.value=(String)a.getAttribute(attr);

					ek.attrs.put(attr.toString(), ak);

					logger.debug("attribute "+attr.toString()+" "+ak.value);
				}
				else
					logger.debug("ignored attribute "+obj);
			}

			XmlVElement ve=(XmlVElement)doc.createElement(ek);

			if(root==null)
			{
				root=ve;
				doc.add(ve);
			}
			else
				current.add(ve);

			current=ve;

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }

    @Override
    public void handleText(char[] data, int pos)
    {
		try
		{
			logger.debug("handleText \""+new String(data)+"\" "+pos);

			TextKey tk=new TextKey();
			tk.text=new String(data);

			current.add(builder.createText(tk));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
}
