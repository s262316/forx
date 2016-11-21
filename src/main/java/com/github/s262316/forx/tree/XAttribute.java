package com.github.s262316.forx.tree;

public interface XAttribute extends XNode
{
    @Override
	public NodeType type();
    @Override
	public String getName();
    @Override
	public String getValue();
}
