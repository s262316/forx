package com.github.s262316.forx.tree.resource;

import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class Resource
{
    private URL url;
    private Charset charset;
    private Reader reader;

    public Resource(Charset charset, Reader reader, URL url)
    {
        this.charset = charset;
        this.reader = reader;
        this.url = url;
    }

    public Charset getCharset()
    {
        return charset;
    }

    public Reader getReader()
    {
        return reader;
    }

    public URL getUrl()
    {
        return url;
    }
}
