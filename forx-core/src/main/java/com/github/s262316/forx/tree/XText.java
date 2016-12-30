package com.github.s262316.forx.tree;

public interface XText extends XNode
{
    @Override
	public NodeType type();
    @Override
	public String getName();
    @Override
	public String getValue();
}
