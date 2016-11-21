package com.github.s262316.forx.tree;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public interface XElement extends XNode
{
    @Override
	public NodeType type();
    @Override
	public String getName();
    @Override
	public String getValue();

	public List<XNode> getMembers();
	@Deprecated
	public Set<Entry<String, XAttribute>> getAttrs();
	public Map<String, XAttribute> getAttributes();
	public int size();
	public XAttribute getAttr(String name);
	public int attrsSize();
	public void complete(boolean full);
}

