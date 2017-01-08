package com.github.s262316.forx.net;

import com.github.s262316.forx.tree.LinkingMechanism;
import com.github.s262316.forx.tree.ReferringDocument;

import java.io.IOException;

public interface ResourceLoader
{
	public Resource load(String url, ReferringDocument referrer) throws IOException;
}
