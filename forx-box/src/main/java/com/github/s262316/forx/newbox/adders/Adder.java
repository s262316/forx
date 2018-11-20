package com.github.s262316.forx.newbox.adders;

import com.github.s262316.forx.newbox.Box;
import com.github.s262316.forx.newbox.Inline;
import com.github.s262316.forx.newbox.PropertiesEndPoint;
import com.github.s262316.forx.newbox.SpaceFlag;

public interface Adder
{
	void add(Box newChild);
	void add(Inline newChild);
	void add(String string, SpaceFlag notSpace);
}

