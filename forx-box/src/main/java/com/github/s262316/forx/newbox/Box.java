package com.github.s262316.forx.newbox;

import com.github.s262316.forx.newbox.relayouter.LayoutResult;

import java.util.Optional;

public interface Box
{
	// no flow methods because Text and InlineContainerBox can't flow
    SizeResult computeDimensions();
    LayoutResult calculatePosition(Box member);
    void uncalculatePosition(Box member);
	int getId();
	Optional<PropertiesEndPoint> propertiesEndpoint();
	
	int height();
	int width();
	int left();
	int right();
	int bottom();
	int top();
	void setDimensions(int width, int height);
	void setPosition(int x, int y);
}
