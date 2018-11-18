package com.github.s262316.forx.newbox;

import java.awt.Font;

public interface InterBoxOps
{
	Font containerFont();
	void doLoadingLayout(Box child);
	void memberWasAdded(Box newMember);
	void memberWasAdded(Inline newMember);
	void invalidatePosition(Box member);
}
