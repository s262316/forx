package com.github.s262316.forx.tree;

import java.net.URL;

import com.github.s262316.forx.tree.events2.Event;
import com.github.s262316.forx.tree.events2.Listener;
import com.github.s262316.forx.tree.events2.PropagationType;
import com.github.s262316.forx.tree.XmlNode;

public interface XDocument extends XNode
{
	public XElement getRoot();
	public boolean full();
	public void complete(boolean full);
	public XElement findElementById(String id);
	public URL getLocation();
    public void dispatchEvent(XmlNode target, Event event);
    public <T extends Event> void addListener(XmlNode listenee, PropagationType propagation, Class<T> eventType, Listener<? extends Event> listener);
}
