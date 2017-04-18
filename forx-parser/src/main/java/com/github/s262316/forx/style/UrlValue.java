package com.github.s262316.forx.style;

import java.net.URL;

public class UrlValue extends Value
{
    public URL url;

    public UrlValue()
    {
        super(false);
        url=null;
    }

    public UrlValue(URL u)
    {
        super(true);
        url=u;
    }

    @Override
    public String toString()
    {
		return "url : " + url + "\n";
    }

    @Override
    public boolean equals(Object v)
    {
		boolean e=false;
		UrlValue uv=(UrlValue)v;

		if(uv.url.equals(url))
				e=true;

		return e;
    }

}
