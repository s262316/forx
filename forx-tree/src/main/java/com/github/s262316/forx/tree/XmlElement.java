package com.github.s262316.forx.tree;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.tree.events2.MutationType;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;

import com.google.common.base.Preconditions;


public class XmlElement extends XmlNode implements XElement
{
	private final static Logger logger=LoggerFactory.getLogger(XmlElement.class);

	private String name;
	public LinkedList<XNode> members=new LinkedList<XNode>();
	public Map<String, XAttribute> attrs=new HashMap<String, XAttribute>();
	private EventDispatcher eventDispatcher;
	
	public XmlElement(String name, XmlDocument doc, int id, EventDispatcher eventDispatcher)
	{
		super(doc, id);

		this.name=name;
		this.eventDispatcher=eventDispatcher;
	}

    @Override
	public NodeType type()
	{
		return NodeType.X_ELEMENT;
	}

    @Override
	public String getName()
	{
		return name;
	}

    @Override
	public String getValue()
	{
		return "";
	}

    @Override
	public int size()
	{
		return members.size();
	}

    @Override
	public int attrsSize()
	{
		return attrs.size();
	}

    @Override
	public List<XNode> getMembers()
	{
		return Collections.unmodifiableList(members);
	}

    @Deprecated
    @Override
	public Set<Entry<String, XAttribute>> getAttrs()
	{
		return Collections.unmodifiableMap(attrs).entrySet();
	}
    
    @Override
	public Map<String, XAttribute> getAttributes()
	{
		return attrs;
	}

	public void setAttr(String name, String value)
	{
		attrs.put(name, new XmlAttribute(name, value, this.getDocument(), 0)); // TODO id=0
	}

    @Override
	public XAttribute getAttr(String name)
	{
		return attrs.get(name);
	}

	public void add(XmlNode n)
	{
		logger.debug("add");

		XmlNode node=n;
        XmlMutationEvent addEvent=new XmlMutationEvent(MutationType.ADD, node);

		n.setParentNode(this);
		if(!members.isEmpty())
		{
			n.setPreviousNode(members.getLast());
			((XmlNode)members.getLast()).setNextNode(n);
		}
		n.setNextNode(null);

		members.addLast(node);

		logger.debug("dispatching add event");

		getDocument().dispatchEvent(node, addEvent);

        // (2) fire CONNECT to node's attributes
        // (3) fire CONNECT to node's children
       	XmlMutationEvent connectEvent=new XmlMutationEvent(MutationType.CONNECT, node);
       	eventDispatcher.broadcast(node, connectEvent);
	}

	public void remove(XmlNode node)
	{
		Preconditions.checkState(members.contains(node));

        XmlMutationEvent event=new XmlMutationEvent(MutationType.REMOVE, node);
        getDocument().dispatchEvent(node, event);

        if(node.type()==NodeType.X_ELEMENT)
        {
        	XmlMutationEvent connectEvent=new XmlMutationEvent(MutationType.DISCONNECT, node);

        	eventDispatcher.broadcast(node, connectEvent);
        }

		// set the previous node's new next field
		if(node.previousNode()!=null)
			((XmlNode)node.previousNode()).setNextNode(node.nextNode());
		// set the next node's new prev field
		if(node.nextNode()!=null)
			((XmlNode)node.nextNode()).setPreviousNode(node.previousNode());

		members.remove(node);
	}

	public void insert(XmlNode newNode, XmlNode beforeNode)
	{
	}

    @Override
    public void complete(boolean full)
    {
    }



}



