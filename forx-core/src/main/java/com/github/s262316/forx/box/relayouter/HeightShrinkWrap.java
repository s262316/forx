package com.github.s262316.forx.box.relayouter;

import java.util.List;

import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.Margins;
import com.github.s262316.forx.box.util.Sizes;

import com.google.common.collect.Iterables;

public class HeightShrinkWrap implements RelayouterCallback
{
	@Override
	public void postLaid(Layable laid)
	{
		Layable container=LayableContainerMapping.getContainer(laid).getContainer();
		List<Layable> members=container.getMembersAll();
		
		if(Iterables.getLast(members)==laid)
		{
			int top, bottom;
			Layable first=members.get(0);
			Layable last=Iterables.getLast(members);
			
			if(Margins.doesTopMarginCollapseWithParent(first))
				top=container.top();
			else
				top=first.top();
				
			if(Margins.doesBottomMarginCollapseWithParent(last))
				bottom=container.bottom();
			else
				bottom=last.bottom();
				
			container.set_dimensions(laid.width(), Sizes.length(top, bottom));
		}
	}	
}
