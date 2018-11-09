package com.github.s262316.forx.tree.visual;

import java.util.Optional;

import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Inline;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.util.Boxes;

public class BlockBoxParentLocator implements ParentLocator
{
	private BlockBox blockBox;
	private Visual visual;

	public BlockBoxParentLocator(BlockBox blockBox, Visual visual)
	{
		this.blockBox=blockBox;
		this.visual=visual;
	}
	
	@Override
	public Box locate(Box newChild)
	{
		if (BoxTypes.isInline(newChild) == true)
		{
			// InlineBox, InlineBlockBox
			
			Optional<InlineBox> last=Boxes.getLastFlowMemberAnAnonInlineContainer(blockBox);
			if(!last.isPresent())
			{
				// need to add this inline to a dummy inline
				InlineBox dummy = visual.createAnonInlineBox(AnonReason.INLINE_CONTAINER);
				blockBox.flow_back((Box)dummy);
	
				return dummy;
			}
			else
			{
				return last.get();
			}
		}

		// any other box use this as the parent
		return blockBox;
	}
	
	@Override
	public Box locate(Inline newChild)
	{
		if (BoxTypes.isAtomic(newChild) == true)
		{
			// InlineBox, InlineBlockBox
			
			Optional<InlineBox> last=Boxes.getLastFlowMemberAnAnonInlineContainer(blockBox);
			if(!last.isPresent())
			{
				// need to add this inline to a dummy inline
				InlineBox dummy = visual.createAnonInlineBox(AnonReason.INLINE_CONTAINER);
				blockBox.flow_back((Box)dummy);
	
				return dummy;
			}
			else
			{
				return last.get();
			}			
		}
		
		// must be InlineBox which is not allowed here
		throw new IllegalArgumentException();
	}
}
