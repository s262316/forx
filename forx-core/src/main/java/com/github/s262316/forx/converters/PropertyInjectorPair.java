package com.github.s262316.forx.converters;


public class PropertyInjectorPair<MT>
{
	private String property;
	private ModelValueInjector<MT> injector;
	
	public PropertyInjectorPair(String property, ModelValueInjector<MT> injector)
	{
		this.property = property;
		this.injector = injector;
	}

	public String getProperty()
	{
		return property;
	}

	public ModelValueInjector<MT> getInjector()
	{
		return injector;
	}
}