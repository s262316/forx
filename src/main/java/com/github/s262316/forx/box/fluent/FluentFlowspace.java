package com.github.s262316.forx.box.fluent;

import com.github.s262316.forx.box.Flowspace;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.Line;

import com.google.common.base.Optional;

public class FluentFlowspace
{
	private Flowspace flowSpace;
	
	private FluentFlowspace(Flowspace flowSpace)
	{
		this.flowSpace=flowSpace;
	}
	
	public Optional<FluentLine> lastLine(InlineBox inlinebox)
	{
		Line lastLine=null;
		
		if(flowSpace.line_count(inlinebox)>0)
			lastLine=flowSpace.back_line(inlinebox);
			
		return Optional.fromNullable(lastLine).transform(new LineToFluentLineTransform(flowSpace));
	}
	
	public static FluentFlowspace from(Flowspace flowSpace)
	{
		return new FluentFlowspace(flowSpace);
	}
}

