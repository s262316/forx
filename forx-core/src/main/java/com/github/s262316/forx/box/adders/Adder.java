package com.github.s262316.forx.box.adders;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Inline;

public interface Adder
{
	Box locate(Box newChild);
	Box locate(Inline newChild);
}
