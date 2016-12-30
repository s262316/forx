package com.github.s262316.forx.box.fluent;

import com.github.s262316.forx.box.Flowspace;
import com.github.s262316.forx.box.Line;

import com.google.common.base.Function;

public class LineToFluentLineTransform implements Function<Line, FluentLine>
{
	private Flowspace flowSpace;
	
	public LineToFluentLineTransform(Flowspace flowSpace)
	{
		this.flowSpace=flowSpace;
	}
	
	@Override
	public FluentLine apply(Line line)
	{
		return FluentLine.from(flowSpace, line);
	}
}
