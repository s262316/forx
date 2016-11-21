package com.github.s262316.forx.box.relayouter;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.AbsoluteBox;
import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.FloatBox;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.RootBox;
import com.github.s262316.forx.box.Text;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public class LayableContainerMapping
{
	private final static Logger logger=LoggerFactory.getLogger(LayableContainerMapping.class);
	
	private static final ImmutableMap<Class<? extends Layable>, Class<? extends LayableContainer>> CONTAINER_MAPPING=
	       new ImmutableMap.Builder<Class<? extends Layable>,  Class<? extends LayableContainer>>()
	           .put(AbsoluteBox.class, DefaultLayableContainer.class)
	           .put(FloatBox.class, DefaultLayableContainer.class)
	           .put(BlockBox.class, DefaultLayableContainer.class)
	           .put(InlineBox.class, DefaultLayableContainer.class)
	           .put(Text.class, DefaultLayableContainer.class)
	           .put(RootBox.class, RootBoxContainer.class)
	           .build();

	public static LayableContainer getContainer(Layable layable)
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

