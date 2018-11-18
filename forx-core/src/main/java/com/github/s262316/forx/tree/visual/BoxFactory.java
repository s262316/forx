package com.github.s262316.forx.tree.visual;

import com.github.s262316.forx.newbox.BlockBox;
import com.github.s262316.forx.newbox.Box;
import com.github.s262316.forx.newbox.ReplaceableBoxPlugin;
import com.github.s262316.forx.newbox.Visual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoxFactory
{
	private final static Logger logger=LoggerFactory.getLogger(BoxFactory.class);

	private BoxFactory()
	{}

	public static Box createBlockFlowBox(Visual element, ReplaceableBoxPlugin plugin)
	{
	//	ContentDrawer *cd=new ContentDrawer;
	//	BorderDrawer *bd=new BorderDrawer(cd);
	  //  FillDrawer *fd=new FillDrawer(bd);

		BlockBox box=new BlockBox(element, null, plugin);

//		System.out.println("newblock " + box.id);


		return box;
	}

	public static Box createInlineFlowBox(Visual element)
	{
		InlineBox box=new InlineBox(element, null, null, null);

//		System.out.println("newinline " + box.id);


		return box;
	}

	public static InlineBox createAnonymousInlineFlowBox(AnonVisual anon)
	{
		InlineBox box;

		box=new InlineBox(anon, null, null, null);

//		System.out.println("newanoninlinebox " + box.id);


		return box;
	}

	public static BlockBox createAnonymousBlockFlowBox(AnonVisual anon)
	{
		BlockBox box;

		box=new BlockBox(anon, null, null);

//		System.out.println("newanonblockbox " + box.id);


		return box;
	}

	public static Box createRootBox(Visual element)
	{
	//	ContentDrawer *cd=new ContentDrawer;
	//	BorderDrawer *bd=new BorderDrawer(cd);
	//	FillDrawer *fd=new FillDrawer(bd);

		RootBox box=new RootBox(element, null);
		box.set_container(null);
		box.computeProperties();

		box.selfCalculatePosition();

		logger.debug("createRootBox");

		return box;
	}
}
