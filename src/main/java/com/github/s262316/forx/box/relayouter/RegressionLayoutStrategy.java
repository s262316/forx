package com.github.s262316.forx.box.relayouter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.RootBox;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class RegressionLayoutStrategy
{
	private final static Logger logger=LoggerFactory.getLogger(RegressionLayoutStrategy.class);
	
	private Relayouter initial;
	
	public RegressionLayoutStrategy()
	{
	}
	
	public void layout()
	{
		Preconditions.checkState(initial!=null, "An initial layouter is required");
		
		Relayouter layouter=initial;
		Relayouter lastLayouter=null;
		LayoutResult layoutResult;
		LayoutContext context=new LayoutContext();
		
		do
		{
			logger.debug("using relayouter {} ", layouter);

			Preconditions.checkState(!Objects.equal(lastLayouter, layouter), "Detected infinite loop in layout engine %s %s", lastLayouter, layouter);
			
			layouter.unlayout(context);
			layoutResult=layouter.layout(context);
			
			lastLayouter=layouter;
			layouter=layoutResult.getRelayouter().orNull();
		}
		while(layoutResult.getRelayouter().isPresent());
	}

	public RegressionLayoutStrategy withMoreWidth(Layable subject, Layable cause, int delta)
	{
		initial=new MoreWidthLayouter(subject, cause, delta);
		return this;
	}

	public RegressionLayoutStrategy withMoreWidthDontUnderstandWhy(Layable subject, Layable cause, int delta)
	{
		initial=new MoreWidthLayouter(subject, cause, delta);
		return this;
	}

	public RegressionLayoutStrategy withIncreaseCollapsedTopMargin(Layable subject, Layable topmostAffected, int amount)
	{
		return null;
	}

	public RegressionLayoutStrategy withIncreaseAllCollapsedTopMargins(Layable subject, Layable topmostAffected, int amount)
	{
		return null;
	}

	public RegressionLayoutStrategy withMoreHeightDontUnderstandWhy(Layable subject, Layable cause, int amount)
	{
		initial=new MoreHeightLayouter(subject, cause, amount);
		return this;
	}

	public RegressionLayoutStrategy withMoreHeight(Layable subject, Layable cause, int amount)
	{
		initial=new MoreHeightLayouter(subject, cause, amount);
		return this;
	}

	public RegressionLayoutStrategy withLoadingLayouter(Layable newBox)
	{
		initial=new SingleLayouter(newBox);
		return this;
	}

	public RegressionLayoutStrategy withLoadingTableLayouter(Layable tableRoot, Layable newBox)
	{
		return null;
	}
	
	public RegressionLayoutStrategy full(Layable root)
	{
		initial=new FullLayouter((RootBox)root);
		return this;
	}
	
}
