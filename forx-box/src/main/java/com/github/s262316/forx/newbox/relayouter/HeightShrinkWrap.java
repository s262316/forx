package com.github.s262316.forx.newbox.relayouter;

import java.util.List;

import com.github.s262316.forx.newbox.Box;
import com.github.s262316.forx.newbox.util.Sizes;
import com.google.common.collect.Iterables;

public class HeightShrinkWrap implements RelayouterCallback
{
	@Override
	public void postLaid(Box laid)
	{
		Box container=LayableContainerMapping.getContainer(laid).getContainer();
		List<Box> members=container.getInterBoxOps().getMembers();
		
		if(Iterables.getLast(members)==laid)
		{
			int top, bottom;
			Box first=members.get(0);
			Box last=Iterables.getLast(members);
			
			top=first.top();
			bottom=last.bottom();
			
			container.setDimensions(container.width(), Sizes.length(top, bottom));
		}
	}	
}
