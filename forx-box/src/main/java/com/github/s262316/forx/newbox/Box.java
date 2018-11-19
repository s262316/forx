package com.github.s262316.forx.newbox;

import java.util.List;
import java.util.Optional;

import com.github.s262316.forx.newbox.relayouter.LayoutResult;

public interface Box
{
	// no flow methods because Text and InlineContainerBox can't flow
    SizeResult computeDimensions();
    LayoutResult calculatePosition(Box member);
    void uncalculatePosition(Box member);
	Optional<PropertiesEndPoint> propertiesEndpoint();
	
	int height();
	int width();
	int left();
	int right();
	int bottom();
	int top();
	void setDimensions(int width, int height);
	void setPosition(int x, int y);
	void unsetPosition();
	
	int getId();
	void setId(int id);
	
	void setFutureWidth(int newWidth);
	void setFutureHeight(int newHeight);
	
	InterBoxOps getInterBoxOps();
}
