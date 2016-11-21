package com.github.s262316.forx.tree.events2;

import com.github.s262316.forx.tree.impl.XmlNode;

public class XmlMutationEvent implements Event
{
	private MutationType type;
	private XmlNode subject;
	
	public XmlMutationEvent(MutationType type, XmlNode subject)
	{
		this.type = type;
		this.subject = subject;
	}

	public MutationType getType()
	{
		return type;
	}

	public XmlNode getSubject()
	{
		return subject;
	}
}
