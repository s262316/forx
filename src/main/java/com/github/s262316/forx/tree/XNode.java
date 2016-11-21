package com.github.s262316.forx.tree;

import com.github.s262316.forx.tree.events2.Event;
import com.github.s262316.forx.tree.events2.Listener;
import com.github.s262316.forx.tree.events2.XMutationListener;

public interface XNode
{
	public NodeType type();
	public String getName();
	public String getValue();
	public XDocument getDocument();
	@Deprecated
	public void addMutationListener(XMutationListener mutationListener);
	public XNode parentNode();
	public boolean isConnected();
	public String lang();
	public XNode previousNode();
	public XNode nextNode();
	public <T> T getProperty(String prop, Class<T> clazz);
	public void setProperty(String prop, Object val);
	public void addListener(Listener<? extends Event> listener, Class<? extends Event> eventClass);
}
