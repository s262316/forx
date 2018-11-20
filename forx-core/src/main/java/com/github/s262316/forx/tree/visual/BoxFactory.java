package com.github.s262316.forx.tree.visual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.newbox.BlockBox;
import com.github.s262316.forx.newbox.Box;
import com.github.s262316.forx.newbox.InlineHeadless;
import com.github.s262316.forx.newbox.ReplaceableBoxPlugin;
import com.github.s262316.forx.newbox.RootBox;
import com.github.s262316.forx.newbox.Visual;

public class BoxFactory
{
	private final static Logger logger=LoggerFactory.getLogger(BoxFactory.class);

	private BoxFactory()
	{}

	public static BlockBox createBlockFlowBox(Visual visual, ReplaceableBoxPlugin plugin)
	{
		BlockBox box=new BlockBox(visual, null);

		return box;
	}

	public static InlineHeadless createInlineFlowBox(Visual visual)
	{
		InlineHeadless box=new InlineHeadless(visual, null);

		return box;
	}

	public static BlockBox createAnonymousBlockFlowBox(AnonVisual anon)
	{
		BlockBox box=new BlockBox(anon, null);

		return box;
	}

	public static InlineHeadless createAnonymousInlineFlowBox(AnonVisual anon)
	{
		InlineHeadless box=new InlineHeadless(anon, null);

		return box;
	}	
	
	public static RootBox createRootBox(Visual element)
	{
		RootBox box=new RootBox(element, null);
		box.computeProperties();
		box.selfCalculatePosition();
		
		return box;
	}
}
