package com.github.s262316.forx.newbox.adders;

import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Inline;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.util.Boxes;
import com.github.s262316.forx.tree.visual.AnonReason;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.graph.Traverser;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class InlineBoxParentAdder implements Adder
{
	private final static Logger logger=LoggerFactory.getLogger(InlineBoxParentAdder.class);
	
	private InlineBox inlineBox;

	public InlineBoxParentAdder(InlineBox inlineBox)
	{
		this.inlineBox=inlineBox;
	}
	
	@Override
	public void add(Box newChild)
	{
		logger.debug("add {} -> {}", inlineBox, newChild);

		if(BoxTypes.isBlockBox(newChild) == true &&
				inlineBox.getVisual().getPostSplit()==null)
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
			
			// new box goes AFTER anonInlineContainer inside anonBlockContainer
			anonBlockContainer.flow_back(newChild);			
			
			// last-inline structure must be duplicated and added to anonBlockContainer

			Predicate<Layable> isAncestor=toRoot::contains;
			Predicate<Layable> isAfter=v -> v.getId() > inlineBox.getId();

			List<Box> preSplitBoxes=StreamSupport.stream(Traverser.<Layable>forTree(v -> v.getMembersAll())
				.depthFirstPreOrder(anonInlineContainer).spliterator(), false)
				.filter(Predicates.instanceOf(Box.class))
				.map(v -> BoxTypes.toBox(v))
				.filter(isAncestor.or(isAfter))
				.collect(Collectors.toList());

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
		}
		else if(BoxTypes.isBlockBox(newChild) == true)
		{
			// the split structure has already been created - locate the insert position
			
			List<Box> toRoot=Boxes.routeToRoot(inlineBox);
			
			int blockPosition=Iterables.indexOf(toRoot, v -> BoxTypes.isBlockBox(v));
			Validate.isTrue(blockPosition > 0);

			BlockBox anonBlockContainer=(BlockBox)toRoot.get(blockPosition);

			// get the position before the post-split-inline-container
			Box insertBefore=anonBlockContainer.getMembersAll().stream()
				.filter(v -> BoxTypes.isBox(v))
				.map(v -> BoxTypes.toBox(v))
				.filter(v -> v.getVisual().getAnonReason()==AnonReason.BLOCK_INSIDE_INLINE_POST_SPLIT_STRUCTURE)
				.findFirst().get();

			anonBlockContainer.flow_insert(newChild, insertBefore);
		}
		else
		{
			inlineBox.flow_back(newChild);
		}
	}

	@Override
	public void add(Inline newChild)
	{
		logger.debug("add {} -> {}", inlineBox, newChild);

		inlineBox.flow_back(newChild);
	}
}

