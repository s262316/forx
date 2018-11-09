package com.github.s262316.forx.tree.visual;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Inline;

public interface ParentLocator
{
	Box locate(Box newChild);
	Box locate(Inline newChild);
}
