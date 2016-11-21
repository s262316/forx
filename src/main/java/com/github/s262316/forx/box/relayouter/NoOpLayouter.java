package com.github.s262316.forx.box.relayouter;

import com.google.common.base.Optional;

public class NoOpLayouter implements Relayouter
{

	@Override
	public void unlayout(LayoutContext context)
	{
	}

	@Override
	public LayoutResult layout(LayoutContext context)
	{
		return new LayoutResult(true, Optional.<Relayouter>absent());
	}
}
