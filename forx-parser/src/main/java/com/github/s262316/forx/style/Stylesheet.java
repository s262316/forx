package com.github.s262316.forx.style;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.github.s262316.forx.tree.ReferringDocument;
import com.google.common.base.Optional;
import com.github.s262316.forx.tree.XElement;
import com.github.s262316.forx.style.selectors.PseudoElementType;
import com.github.s262316.forx.style.selectors.Selector;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Stylesheet implements ReferringDocument
{
    private TreeSet<StyleRule> ruleset=new TreeSet<StyleRule>(new StyleRuleComparator().reverse());
    private URL location;
    private Charset charset;

    public Stylesheet(Charset charset, URL location)
    {
        this.charset = charset;
        this.location = location;
    }

    /**
     * @param stylesheet
     * @param important only merge important/not-important declarations
     */
    private void mergeRules(Stylesheet stylesheet, boolean important)
    {
        StyleRule added;

        for(StyleRule sr : stylesheet.ruleset)
        {
            // create a stylerule with just the normal or important declarations
            // that we're after        	
        	ImmutableMap<String, Declaration> copiedDecs=ImmutableMap.copyOf(
        			Maps.filterValues(sr.getDeclarations(), v -> v.isImportant()==important));
        	
        	if(!copiedDecs.isEmpty())
        	{
	            added=new StyleRule(sr.getSelector(), copiedDecs, sr.getMedia(), sr.getOrder());
	            ruleset.add(added);
        	}
        }
    }

    public void mergeNormalRules(Stylesheet stylesheet)
    {
        mergeRules(stylesheet, false);
    }

    public void mergeImportantRules(Stylesheet stylesheet)
    {
        mergeRules(stylesheet, true);
    }
    
    public void reset()
    {
    	ruleset.clear();
    }
    
    public Declaration findDeclaration(XElement element, String property, MediaType mediaType)
    {
        return findDeclaration(element, property, mediaType, PseudoElementType.PE_NOT_PSEUDO);
    }

    public Declaration findDeclaration(XElement element, String property, MediaType mediaType, PseudoElementType pseudoType)
    {
        Declaration d2=null;
        StyleRule sr;
        EnumSet<MediaType> searchFor=EnumSet.of(MediaType.MT_ALL, mediaType);
        
        Iterator<StyleRule> it=ruleset.iterator();
        while(it.hasNext())
        {
            sr=it.next();

            Selector sel=sr.getSelector();
            if(
            	(mediaType==MediaType.MT_ALL || !Sets.intersection(searchFor, sr.getMedia()).isEmpty())
            		&&
            	sel.isMatch(element, pseudoType))
            {
            	if(sr.getDeclarations().containsKey(property))
            		return sr.getDeclarations().get(property);
            }
        }

        return d2;
    }
    
    public List<StyleRule> findAllRules(XElement element, MediaType mediaType, PseudoElementType pseudoType)
    {
        StyleRule sr;
        boolean found=false;
        EnumSet<MediaType> searchFor=EnumSet.of(MediaType.MT_ALL, mediaType);
        List<StyleRule> matching=new ArrayList<>();

        Iterator<StyleRule> it=ruleset.iterator();
        while(it.hasNext()&&!found)
        {
        	sr=it.next();

            Selector sel=sr.getSelector();
            if(
            	(mediaType==MediaType.MT_ALL || !Sets.intersection(searchFor, sr.getMedia()).isEmpty())
            		&&
            	sel.isMatch(element, pseudoType))
            {
            	matching.add(sr);
            }
        }

        return matching;
    }
    
    public TreeSet<StyleRule> getRuleset()
	{
		return ruleset;
	}

    @Override
    public Optional<Charset> getCharset()
    {
        return Optional.of(charset);
    }

    @Override
    public URL getLocation()
    {
        return location;
    }

    @Override
    public String toString()
    {
		return Objects.toStringHelper(this).addValue(ruleset).toString();
    }
}

