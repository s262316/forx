package com.github.s262316.forx.fluent;

import com.github.s262316.forx.box.AtomicInline;
import com.github.s262316.forx.box.Flowspace;
import com.github.s262316.forx.box.Line;

import com.google.common.base.Optional;

public class FluentLine
{
	private Line line;
	private Flowspace flowSpace;
	
	private FluentLine(Flowspace flowSpace, Line line)
	{
		this.line=line;
		this.flowSpace=flowSpace;
	}
	
	public Optional<AtomicInline> lastAtomic()
	{
		if(flowSpace!=null)
			return Optional.fromNullable(flowSpace.back_atomic(line));
		else
			return Optional.absent();
	}
	
	public Optional<AtomicInline> firstAtomic()
	{
		if(flowSpace!=null)
			return Optional.fromNullable(flowSpace.front_atomic(line));
		else
			return Optional.absent();
	}
	
	public Line line()
	{
		return line;
	}
	
	public static FluentLine from(Flowspace flowSpace, Line line)
	{
		return new FluentLine(flowSpace, line);
	}
	
	public static final FluentLine DEAD=new FluentLine(null, null);
}
