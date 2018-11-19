package com.github.s262316.forx.newbox.relayouter;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.newbox.BlockBox;
import com.github.s262316.forx.newbox.Box;
import com.github.s262316.forx.newbox.InlineContainerBox;
import com.github.s262316.forx.newbox.Text;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public class LayableContainerMapping
{
	private final static Logger logger=LoggerFactory.getLogger(LayableContainerMapping.class);
	
	private static final ImmutableMap<Class<? extends Box>, Class<? extends LayableContainer>> CONTAINER_MAPPING=
	       new ImmutableMap.Builder<Class<? extends Box>,  Class<? extends LayableContainer>>()
	           .put(BlockBox.class, DefaultLayableContainer.class)
	           .put(Text.class, DefaultLayableContainer.class)
	           .put(InlineContainerBox.class, DefaultLayableContainer.class)
	           .build();

	public static LayableContainer getContainer(Box layable)
	{
		try
		{
			Preconditions.checkState(CONTAINER_MAPPING.containsKey(layable.getClass()), layable.getClass());
			
			Class<? extends LayableContainer> containerClass=CONTAINER_MAPPING.get(layable.getClass());
			LayableContainer container=ConstructorUtils.invokeConstructor(containerClass, layable);
			
			return container;
		}
		catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e)
		{
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}
}

