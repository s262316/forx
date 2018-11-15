package com.github.s262316.forx.newbox;

import com.github.s262316.forx.newbox.relayouter.LayoutResult;

public interface Box
{
	// no flow methods because Text and InlineContainerBox can't flow
    SizeResult computeDimensions();
    LayoutResult calculatePosition(Box member);
    void uncalculatePosition(Box member);
	int getId();
	boolean isPropertiesEndpoint();
}
