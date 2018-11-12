package com.github.s262316.forx.tree.visual;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Inline;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.relayouter.LayableTreeTraverser;
import com.github.s262316.forx.box.relayouter.util.AfterOrEqualsLayable;
import com.github.s262316.forx.box.util.Boxes;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

public class InlineBoxParentLocator implements ParentLocator
{
	private final static Logger logger=LoggerFactory.getLogger(InlineBoxParentLocator.class);
	
	private InlineBox inlineBox;
	private Visual visual;

	public InlineBoxParentLocator(InlineBox inlineBox, Visual visual)
	{
		this.inlineBox=inlineBox;
		this.visual=visual;
	}
	
	@Override
	public Box locate(Box newChild)
	{
		if(BoxTypes.isBlockBox(newChild) == true)
		{
			// adding a BlockBox to an InlineBox
			List<Box> toRoot=Boxes.routeToRoot(inlineBox);
			
			int blockPosition=Iterables.indexOf(toRoot, v -> BoxTypes.isBlockBox(v));
			Validate.isTrue(blockPosition > 0);
			
			BlockBox firstBlock=(BlockBox)toRoot.get(blockPosition);
			InlineBox lastInline=(InlineBox)toRoot.get(blockPosition-1);

			// remove last-inline
			firstBlock.remove(lastInline);

			BlockBox anonBlockContainer=firstBlock.getVisual().createAnonBlockBox(AnonReason.BLOCK_INSIDE_INLINE_SPLIT_CONTAINER);
			
			firstBlock.flow_back(anonBlockContainer);
			anonBlockContainer.flow_back((Box)lastInline); // put back the removed inline
			
			// *******
			// new box goes AFTER lastInline inside anonBlockContainer - we must derive this position later
			// *******
			
			// last-inline structure must be duplicated and added to anonBlockContainer
			
			List<Box> postSplitBoxes=new LayableTreeTraverser()
					.preOrderTraversal(lastInline)
					.filter(new AfterOrEqualsLayable(lastInline))
					.filter(Box.class)
					.toList();

			// first Visual is the anon-block-container
			Visual visualToUse=anonBlockContainer.getVisual();
			Box parentBoxToUse=anonBlockContainer;
			for(Box b : postSplitBoxes)
			{
				Box newBox=cloneStructureAndLink(b, visualToUse);
				parentBoxToUse.flow_back(newBox);
				
				// subsequent Visual's is the previous box that was created
				parentBoxToUse=newBox;
				visualToUse=newBox.getVisual();
			}

			return anonBlockContainer;
		}
		else
			return inlineBox;
	}
	
	public Box cloneStructureAndLink(Box inPreSplit, Visual visual)
	{
		logger.debug("cloneStructureAndLink {}", inPreSplit);

		// think we will only ever have InlineBox's in here(?)
		Preconditions.checkArgument(inPreSplit instanceof InlineBox);

		InlineBox postSplitInlineBox=visual.createAnonInlineBox(AnonReason.BLOCK_INSIDE_INLINE_POST_SPLIT_STRUCTURE);
		inPreSplit.getVisual().setPostSplit(postSplitInlineBox);

		Validate.notNull(postSplitInlineBox);
		
		return postSplitInlineBox;
	}

	@Override
	public Box locate(Inline newChild)
	{
		return inlineBox;
	}
}
