package com.github.s262316.forx.box.properties.converters;

import com.github.s262316.forx.tree.style.Value;

@FunctionalInterface
public interface ModelToBoxConverter<MT>
{
	public MT convert(Value value);
}
