package com.github.s262316.forx.box.adders;

import java.util.Optional;

import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Inline;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.util.Boxes;
import com.github.s262316.forx.tree.visual.AnonReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockBoxParentAdder implements Adder
{
	private final static Logger logger= LoggerFactory.getLogger(BlockBoxParentAdder.class);

	private BlockBox blockBox;

	public BlockBoxParentAdder(BlockBox blockBox)
	{
		this.blockBox=blockBox;
	}
	
	@Override
	public void add(Box newChild)
	{
		logger.debug("add {} -> {}", blockBox, newChild);

		if (BoxTypes.isInline(newChild) == true)
		{
			// InlineBox, InlineBlockBox
			
			Optional<InlineBox> last=Boxes.getLastFlowMemberAnAnonInlineContainer(blockBox);
			if(!last.isPresent())
			{
				// need to add this inline to a dummy inline
				InlineBox dummy = blockBox.getVisual().createAnonInlineBox(AnonReason.INLINE_CONTAINER);
				blockBox.flow_back((Box)dummy);
				dummy.flow_back(newChild);
			}
			else
				last.get().flow_back(newChild);
		}
		else
			blockBox.flow_back(newChild);
	}
	
	@Override
	public void add(Inline newChild)
	{
		logger.debug("add {} -> {}", blockBox, newChild);

		if (BoxTypes.isAtomic(newChild) == true)
		{
			// InlineBox, InlineBlockBox
			
			Optional<InlineBox> last=Boxes.getLastFlowMemberAnAnonInlineContainer(blockBox);
			if(!last.isPresent())
			{
				// need to add this inline to a dummy inline
				InlineBox dummy = blockBox.getVisual().createAnonInlineBox(AnonReason.INLINE_CONTAINER);
				blockBox.flow_back((Box)dummy);
				dummy.flow_back(newChild);
			}
			else
				last.get().flow_back(newChild);
		}
		else
		{
			// must be an InlineBox which is not allowed here
			throw new IllegalArgumentException();
		}
	}
}
