package com.github.s262316.forx.tree.visual;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.tree.visual.mockbox.MockBlockBox;
import com.github.s262316.forx.tree.visual.mockbox.MockInlineBox;

public class SemiMockedBoxTree
{
	public static MockBlockBox f(Node node)
	{
		MockBlockBox thisBox;
		
		if(!(node instanceof RootNode))
			throw new IllegalArgumentException();
			
		thisBox=new MockBlockBox();

		for(Node n : node.nodes)
			f(thisBox, n);
		
		return thisBox;
	}
	
	public static void f(Box parent, Node child)
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
			f(thisBox, n);
	}
}
