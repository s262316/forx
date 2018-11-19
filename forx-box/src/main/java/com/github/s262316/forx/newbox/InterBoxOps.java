package com.github.s262316.forx.newbox;

import java.awt.Font;
import java.util.List;

public interface InterBoxOps
{
	Font containerFont();
	void doLoadingLayout(Box child);
	void memberWasAdded(Box newMember);
	void memberWasAdded(Inline newMember);
	void invalidatePosition(Box member);
	Box getContainer();
	Box getSubject();
	Box getRoot();
	List<Box> getMembers();
}
