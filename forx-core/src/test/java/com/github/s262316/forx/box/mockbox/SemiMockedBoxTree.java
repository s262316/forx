package com.github.s262316.forx.box.mockbox;

import com.github.s262316.forx.box.Box;

public class SemiMockedBoxTree
{
	public static MockBlockBox map(Node node)
	{
		MockBlockBox thisBox;
		
		if(!(node instanceof RootNode))
			throw new IllegalArgumentException();
			
		thisBox=new MockBlockBox();

		for(Node n : node.nodes)
			map(thisBox, n);
		
		return thisBox;
	}
	
	private static void map(Box parent, Node child)
	{
		Box thisBox;
		
		if(child instanceof InlineNode)
		{
			thisBox=new MockInlineBox();
			parent.flow_back(thisBox);
		}
		else if(child instanceof BlockNode)
		{
			thisBox=new MockBlockBox();
			parent.flow_back(thisBox);
		}
		else
			throw new IllegalArgumentException();

		thisBox.set_container(parent);
		
		for(Node n : child.nodes)
			map(thisBox, n);
	}
}
