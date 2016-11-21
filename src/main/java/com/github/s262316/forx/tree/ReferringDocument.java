package com.github.s262316.forx.tree;

import java.net.URL;
import java.nio.charset.Charset;

import com.google.common.base.Optional;

public interface ReferringDocument
{
	public Optional<Charset> getCharset();
	public URL getLocation();
}
