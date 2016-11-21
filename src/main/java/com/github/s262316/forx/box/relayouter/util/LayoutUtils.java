package com.github.s262316.forx.box.relayouter.util;

import com.github.s262316.forx.box.AtomicInline;
import com.github.s262316.forx.box.Dimensionable;
import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.relayouter.RegressionLayoutStrategy;

import com.google.common.base.Preconditions;

public class LayoutUtils
{
	public static void doLoadingLayout(Layable toLay)
	{
	    if(toLay.table_root() != null)
	    	new RegressionLayoutStrategy().withLoadingTableLayouter(toLay.table_root().container(), toLay).layout();
	    else
	    	new RegressionLayoutStrategy().withLoadingLayouter(toLay).layout();
	    
	    validateLaid(toLay);
	}

	public static void validateLaid(Layable lay)
	{
		final String message="Id:%s"; 
		
		Preconditions.checkState(lay.left()!=Dimensionable.INVALID, message, lay.getId());
		Preconditions.checkState(lay.top()!=Dimensionable.INVALID, message, lay.getId());
		if(lay instanceof AtomicInline)
			Preconditions.checkState(((AtomicInline)lay).line()!=null, message, lay.getId());
	}
	
	public static void validateUnlaid(Layable lay)
	{
		final String message="Id:%s"; 
		
		Preconditions.checkState(lay.left()==Dimensionable.INVALID, message, lay);
		Preconditions.checkState(lay.top()==Dimensionable.INVALID, message, lay);
		if(lay instanceof AtomicInline)
			Preconditions.checkState(((AtomicInline)lay).line()==null, message, lay.getId());
	}
	
	public static void invalidatePosition(Layable layable)
	{
		layable.unsetPosition();
	}
	
}
