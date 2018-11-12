package com.github.s262316.forx.tree.visual;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.github.s262316.forx.box.Layable;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.graph.Traverser;
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
			// this should be the anon inline container
			InlineBox anonInlineContainer=(InlineBox)toRoot.get(blockPosition-1);

			// remove last-inline
			firstBlock.remove(anonInlineContainer);

			BlockBox anonBlockContainer=firstBlock.getVisual().createAnonBlockBox(AnonReason.BLOCK_INSIDE_INLINE_SPLIT_CONTAINER);
			
			firstBlock.flow_back(anonBlockContainer);
			anonBlockContainer.flow_back((Box)anonInlineContainer); // put back the removed inline
			
			// *******
			// new box goes AFTER anonInlineContainer inside anonBlockContainer - we must derive this position later
			// *******
			
			// last-inline structure must be duplicated and added to anonBlockContainer

			Predicate<Layable> isAncestor=toRoot::contains;
			Predicate<Layable> isAfter=v -> v.getId() > inlineBox.getId();

			List<Box> preSplitBoxes=StreamSupport.stream(Traverser.<Layable>forTree(v -> v.getMembersAll())
				.depthFirstPreOrder(anonInlineContainer).spliterator(), false)
				.filter(Predicates.instanceOf(Box.class))
				.map(v -> BoxTypes.toBox(v))
				.filter(isAncestor.or(isAfter))
				.collect(Collectors.toList());

//		List<Box> preSplitBoxes=new LayableTreeTraverser()
//					.preOrderTraversal(anonInlineContainer)
//					.filter(new AfterOrEqualsLayable(anonInlineContainer))
//					.filter(Box.class)
//					.toList();

			// first Visual is the anon-block-container
			Map<Box, Box> prePostParents=new HashMap<>();
			prePostParents.put(anonBlockContainer, anonBlockContainer);

			for(Box preSplit : preSplitBoxes)
			{
				Box postParent=prePostParents.get(preSplit.getContainer());
				Box postSplit=postParent.getVisual().createAnonInlineBox(AnonReason.BLOCK_INSIDE_INLINE_POST_SPLIT_STRUCTURE);
				postParent.flow_back(postSplit);

				preSplit.getVisual().setPostSplit((InlineBox)postSplit);

				prePostParents.put(preSplit, postSplit);
			}

			return anonBlockContainer;
		}
		else
			return inlineBox;
	}

	@Override
	public Box locate(Inline newChild)
	{
		return inlineBox;
	}
}
