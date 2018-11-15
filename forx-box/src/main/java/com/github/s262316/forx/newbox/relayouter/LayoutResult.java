package com.github.s262316.forx.newbox.relayouter;

import com.google.common.base.Optional;

public class LayoutResult
{
	private boolean success;
	private Optional<Relayouter> relayouter;
	
	public LayoutResult(boolean success, Optional<Relayouter> relayouter)
	{
		this.success = success;
		this.relayouter = relayouter;
	}

	public boolean isSuccess()
	{
		return success;
	}

	public Optional<Relayouter> getRelayouter()
	{
		return relayouter;
	}
	
	
}
