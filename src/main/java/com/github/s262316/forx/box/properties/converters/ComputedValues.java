package com.github.s262316.forx.box.properties.converters;

import java.awt.Color;

import com.github.s262316.forx.tree.style.ColourValue;
import com.github.s262316.forx.tree.style.NumericValue;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.visual.VElement;

public class ComputedValues
{
	public static <DT> void specifiedValue(String propertyName, VElement subj, Value v, DT domainValue)
	{
		subj.computed_value(propertyName, v);
	}
	
	public static void domainValueAsNumericAbsolute(String propertyName, VElement subj, Value v, int domainValue)
	{
		subj.computed_value(propertyName, new NumericValue(domainValue, "px"));		
	}
	
	public static void domainValueAsColourValue(String propertyName, VElement subj, Value v, Color domainValue)
	{
		subj.computed_value(propertyName, new ColourValue(domainValue.getRed(), domainValue.getGreen(), domainValue.getBlue()));		
	}
	
}
