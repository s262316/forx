package com.github.s262316.forx.newbox;

import com.github.s262316.forx.graphics.GraphicsContext;
import com.github.s262316.forx.tree.visual.AnonReason;

public interface Visual
{
	GraphicsContext getGraphicsContext();

	InlineHeadless createAnonInlineBox(AnonReason anonReason);
	BlockBox createAnonBlockBox(AnonReason anonReason);
}
