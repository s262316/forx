package com.github.s262316.forx.box.properties.converters;

import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.tree.visual.VElement;

public interface ComputedValueSetter<DT>
{
	public void setComputedValue(String propertyName, VElement subj, Value v, DT domainValue);
}
