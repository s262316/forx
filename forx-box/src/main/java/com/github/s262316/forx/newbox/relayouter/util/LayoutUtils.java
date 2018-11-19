package com.github.s262316.forx.newbox.relayouter.util;

import com.github.s262316.forx.newbox.Box;
import com.github.s262316.forx.newbox.Dimensionable;
import com.github.s262316.forx.newbox.relayouter.RegressionLayoutStrategy;
import com.github.s262316.forx.newbox.util.Boxes;
import com.google.common.base.Preconditions;

public class LayoutUtils
{
	public static void doLoadingLayout(Box root, Box toLay)
	{
		// TODO something better than using IDs for box ordering
		Boxes.renumber(root);
		// end sorry

    	new RegressionLayoutStrategy().withLoadingLayouter(toLay).layout();
	    
	    validateLaid(toLay);
	}

	public static void validateLaid(Box lay)
	{
		final String message="Id:%s"; 
		
		Preconditions.checkState(lay.left()!=Dimensionable.INVALID, message, lay.getId());
		Preconditions.checkState(lay.top()!=Dimensionable.INVALID, message, lay.getId());
	}
	
	public static void validateUnlaid(Box lay)
	{
		final String message="Id:%s"; 
		
		Preconditions.checkState(lay.left()==Dimensionable.INVALID, message, lay);
		Preconditions.checkState(lay.top()==Dimensionable.INVALID, message, lay);
	}
	
	public static void invalidatePosition(Box layable)
	{
		layable.unsetPosition();
	}
}
