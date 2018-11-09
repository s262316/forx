package com.github.s262316.forx.tree.visual;

import java.util.List;

import org.apache.commons.lang3.Validate;

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

			// last-inline structure must be duplicated and added to anonBlockContainer
			List<Box> postSplitBoxes=new LayableTreeTraverser()
					.preOrderTraversal(lastInline)
					.filter(new AfterOrEqualsLayable(lastInline))
					.filter(Box.class)
					.transform(v -> cloneStructureAndLink(v))
					.toList();

			// remove last-inline
			firstBlock.remove(lastInline);
						
			BlockBox anonBlockContainer=firstBlock.getVisual().createAnonBlockBox(AnonReason.BLOCK_INSIDE_INLINE_SPLIT_CONTAINER);
			
			firstBlock.flow_back(anonBlockContainer);
			anonBlockContainer.flow_back((Box)lastInline); // put back the removed inline
			
			// new box goes AFTER lastInline inside anonBlockContainer - we must derive this position later
			
			// add each post-structure box
			anonBlockContainer.flow_back(Iterables.getLast(postSplitBoxes));
			for(int i=postSplitBoxes.size()-1; i>0; i--)
			{
				postSplitBoxes.get(i+1).flow_back(postSplitBoxes.get(i));
			}

			return anonBlockContainer;
		}
		else
			return inlineBox;
	}
	
	public Box cloneStructureAndLink(Box inPreSplit)
	{
		// think we will only ever have InlineBox's in here(?)
		Preconditions.checkArgument(inPreSplit instanceof InlineBox);

		InlineBox postSplitInlineBox=inPreSplit.getContainer().getVisual().createAnonInlineBox(AnonReason.BLOCK_INSIDE_INLINE_POST_SPLIT_STRUCTURE);
		postSplitInlineBox.set_container(inPreSplit.getContainer().getVisual().getPostSplit());

		inPreSplit.getVisual().setPostSplit(postSplitInlineBox);

		return postSplitInlineBox;
	}

	@Override
	public Box locate(Inline newChild)
	{
		return inlineBox;
	}
}
