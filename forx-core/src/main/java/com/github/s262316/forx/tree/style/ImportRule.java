package com.github.s262316.forx.tree.style;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;

import com.github.s262316.forx.css.CSSParser;
import com.github.s262316.forx.css.CSSParserException;
import com.github.s262316.forx.css.CSSPropertiesReference;
import com.github.s262316.forx.net.ResourceLoader;

import com.google.common.collect.Sets;
import com.github.s262316.forx.tree.ReferringDocument;

public class ImportRule
{
    private String location;
    private Set<MediaType> media;
    private ResourceLoader resourceLoader;
    private ReferringDocument referringDocument;
    private CSSPropertiesReference cssPropertiesReference;
    
    public ImportRule(String location, Set<MediaType> media, ResourceLoader resourceLoader, ReferringDocument referringDocument, CSSPropertiesReference cssPropertiesReference)
	{
		this.location = location;
		this.media = media;
		this.resourceLoader = resourceLoader;
        this.referringDocument=referringDocument;
        this.cssPropertiesReference=cssPropertiesReference;
	}

	public Stylesheet loadStylesheet(Set<MediaType> usingMediaTypesOf) throws MalformedURLException, IOException, CSSParserException
	{
		if(!Sets.intersection(usingMediaTypesOf, media).isEmpty() ||
                media.contains(MediaType.MT_ALL))
		{
            CSSParser parser=new CSSParser(location, referringDocument, resourceLoader, cssPropertiesReference);
            Stylesheet ss=parser.parse_stylesheet();

	    	return ss;
		}
        else
            return StylesheetFactory.createEmptyStylesheet();
    }
	
    @Override
    public String toString()
    {
        String str="";

        str+="ImportRule :"+location+"\n";
        for(MediaType s : media)
            str+="media: " + s + "\n";

        return str;
    }
}
