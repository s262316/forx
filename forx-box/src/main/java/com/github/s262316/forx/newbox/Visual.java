package com.github.s262316.forx.newbox;

import com.github.s262316.forx.graphics.GraphicsContext;

public interface Visual
{
	GraphicsContext getGraphicsContext();

	InlineHeadless createAnonInlineBox(AnonReason anonReason);
	BlockBox createAnonBlockBox(AnonReason anonReason);

    AnonReason getAnonReason();
	void setPostSplit(PropertiesEndPoint postSplitInlineBox);
	PropertiesEndPoint getPostSplit();	
}
