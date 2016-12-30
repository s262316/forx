package com.github.s262316.forx.box.relayouter;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.relayouter.util.LayoutUtils;
import com.github.s262316.forx.util.ExtractBoxId;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public abstract class AbstractRelayouter implements Relayouter
{
	private final static Logger logger=LoggerFactory.getLogger(AbstractRelayouter.class);
	private static long invoc=0;
	private RelayouterCallback afterLaid;
	
	public abstract List<Layable> getAffected();

	public AbstractRelayouter()
	{
		this.afterLaid=new NoOpRelayouterCallback();
	}

	public AbstractRelayouter(RelayouterCallback afterLaid)
	{
		this.afterLaid = afterLaid;
	}

	private LayoutResult doLayout(LayoutContext context)
	{
		Layable lay;
		LayoutResult layoutResult;
		Iterator<Layable> it=context.getDirties().iterator();
		
		++invoc;
		
		if(logger.isDebugEnabled())
			logger.debug("layout of {}, invoc={}", Joiner.on(',').join(Iterators.transform(context.getDirties().iterator(), new ExtractBoxId())), invoc);
		
		while(it.hasNext())
		{
			lay=it.next();
			
			LayableContainer container=LayableContainerMapping.getContainer(lay);
			layoutResult=container.calculatePosition(lay);
			if(layoutResult.isSuccess())
			{
				LayoutUtils.validateLaid(lay);
				context.notDirty(lay);
				
				afterLaid.postLaid(lay);
			}
			
			if(layoutResult.getRelayouter().isPresent())
				return layoutResult;
		}
		
		return new LayoutResult(true, Optional.<Relayouter>absent());
	}
	
	protected void preLayout()
	{}

	protected void postLayout()
	{}
	
	@Override
	public LayoutResult layout(LayoutContext context)
	{
		LayoutResult relayoutRequired;
		
		preLayout();
		relayoutRequired=doLayout(context);
		
		return relayoutRequired;
	}
	
	@Override
	public void unlayout(LayoutContext context)
	{
		List<Layable> affected=getAffected();

		for(Layable lay : Lists.reverse(affected))
		{
			LayableContainer container=LayableContainerMapping.getContainer(lay);
			
			container.uncalculatePosition(lay);
			context.addToDirty(lay);

			LayoutUtils.validateUnlaid(lay);
		}
	}	
}



