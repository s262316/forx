package com.github.s262316.forx.box.properties.converters;

import java.util.LinkedHashMap;
import java.util.Optional;

import com.google.common.base.Preconditions;

import com.github.s262316.forx.tree.style.MediaType;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.visual.VElement;


public class ModelPropertyBinding<MT>
{
	private MediaType mediaType;
	private PseudoElementType pseudoType;
	private LinkedHashMap<Class<?>, ModelToBoxConverter<MT>> converters=new LinkedHashMap<>();
	private ModelValueInjector<MT> injector;
	private Optional<ComputedValueSetter<MT>> computedValueSetter=Optional.empty();
	
	public ModelPropertyBinding(MediaType mediaType, PseudoElementType pseudoType)
	{
		this.mediaType=mediaType;
		this.pseudoType=pseudoType;
	}
	
	public ModelPropertyBinding<MT> when(Class<?> criteria, ModelToBoxConverter<MT> converter)
	{
		converters.put(criteria, converter);
		
		return this;
	}

	public ModelPropertyBinding<MT> inject(ModelValueInjector<MT> injector)
	{
		this.injector=injector;
		return this;
	}

	public ModelPropertyBinding<MT> computedValue(ComputedValueSetter<MT> computedValueSetter)
	{
		this.computedValueSetter=Optional.of(computedValueSetter);
		return this;
	}

	public void bind(VElement subj, String... propertyNames)
	{
		boolean doneOne=false;
		
		for(String propertyName : propertyNames)
		{
			Value v=subj.getPropertyValue(propertyName, mediaType, pseudoType);
			if(v==null)
				continue;
			
			MT modelValue=Optional.ofNullable(converters.get(v.getClass()))
					.map(conv -> conv.convert(v))
					.orElseThrow(() -> new IllegalArgumentException("No mapping for "+v));
			
			injector.inject(modelValue);
	
			computedValueSetter.ifPresent(t -> t.setComputedValue(propertyName, subj, v, modelValue));

			doneOne=true;
			break;
		}
		
		Preconditions.checkState(doneOne);
	}
	
	public boolean validate(Value value)
	{
		try
		{
			return Optional.ofNullable(converters.get(value.getClass()))
				.map(conv -> conv.convert(value))
				.isPresent();
		}
		catch(IllegalArgumentException iae)
		{
			return false;
		}
	}
}

