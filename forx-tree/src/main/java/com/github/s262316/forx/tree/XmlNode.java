package com.github.s262316.forx.tree;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.tree.events2.Event;
import com.github.s262316.forx.tree.events2.Listener;
import com.github.s262316.forx.tree.events2.XMutationListener;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;

public abstract class XmlNode implements XNode
{
	final static Logger log=LoggerFactory.getLogger(XmlNode.class);
	
	private XmlDocument doc;
	protected int id;
	private XNode prev, next, parent;
	private Map<String, Object> properties;
    public String language;

	public XmlNode(XmlDocument d, int id)
	{
		log.debug("creating "+id);

		this.doc=d;
		this.id=id;
		prev=null;
		next=null;

		properties=new HashMap<String, Object>();
	}

    @Override
	public XmlDocument getDocument()
	{
		return doc;
	}

	public void setDocument(XmlDocument d)
	{
		doc=d;
	}

	public int getId()
	{
		return id;
	}

    @Override
	public <T> T getProperty(String prop, Class<T> clazz)
	{
		return (T)properties.get(prop);
	}

    @Override
	public void setProperty(String prop, Object val)
	{
		properties.put(prop, val);
	}

    @Override
	public String lang()
	{
		String retval;

		if(language.equals("<null>"))
		{
			if(parentNode()!=null)
				retval=parentNode().lang();
			else
				retval="";
		}
		else
			retval=language;

		return retval;
	}

    @Override
	public boolean isConnected()
	{
		XNode parent=this;

		while(parent.parentNode()!=null)
		{
			parent=parent.parentNode();
		}

		return (parent.type()==NodeType.X_DOCUMENT);
	}

    @Deprecated
    @Override
	public void addMutationListener(XMutationListener mutationListener)
	{
    	getDocument().addListener(this, mutationListener.getModelType(), XmlMutationEvent.class, mutationListener);
	}
    
    @Override
	public void addListener(Listener<? extends Event> listener, Class<? extends Event> eventClass)
	{
    	getDocument().addListener(this, listener.getModelType(), eventClass, listener);
	}    

    @Override
	public XNode parentNode()
	{
		return parent;
	}

	public void setParentNode(XmlNode node)
	{
		parent=node;
	}

    @Override
	public XNode previousNode()
	{
		return prev;
	}

    @Override
	public XNode nextNode()
	{
		return next;
	}

	public void setPreviousNode(XNode prev)
	{
		this.prev=prev;
	}

	public void setNextNode(XNode next)
	{
		this.next=next;
	}

}
