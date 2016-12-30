package com.github.s262316.forx.util;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class Functions2
{
	public static <E> Function<Iterable<E>, E> last()
	{
		return new LastOfIterableFunction<E>();
	}
	
	public static <OT, PT> Function<OT, PT> extractProperty(String propertyName)
	{
		return new ExtractPropertyFunction<OT, PT>(propertyName);
	}
	
	static class LastOfIterableFunction<E> implements Function<Iterable<E>, E>
	{
		@Override
		@Nullable
		public E apply(@Nullable Iterable<E> input)
		{
			return Iterables.getLast(input);
		}
	}
	
	static class ExtractPropertyFunction<OT, PT> implements Function<OT, PT>
	{
		private String propertyName;
		
		public ExtractPropertyFunction(String propertyName)
		{
			this.propertyName=propertyName;
		}
		
		@Override
		public PT apply(Object input)
		{
			try
			{
				return (PT)PropertyUtils.getProperty(input, propertyName);
			}
			catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
			{
				throw new RuntimeException("could not access "+propertyName, e);
			}
		}		
	}
}
