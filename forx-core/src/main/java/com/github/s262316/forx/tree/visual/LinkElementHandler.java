package com.github.s262316.forx.tree.visual;

import com.github.s262316.forx.css.CSSParser;
import com.github.s262316.forx.css.CSSParserException;
import com.github.s262316.forx.css.CssLoader;
import com.github.s262316.forx.net.ResourceLoader;
import com.github.s262316.forx.tree.LinkingMechanism;
import com.github.s262316.forx.tree.ReferringDocument;
import com.github.s262316.forx.tree.XAttribute;
import com.github.s262316.forx.tree.XNodes;
import com.github.s262316.forx.tree.events2.MutationType;
import com.github.s262316.forx.tree.events2.PropagationType;
import com.github.s262316.forx.tree.events2.XMutationListener;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;
import com.github.s262316.forx.tree.impl.XmlDocument;
import com.github.s262316.forx.tree.impl.XmlElement;
import com.github.s262316.forx.tree.style.Stylesheet;
import com.github.s262316.forx.tree.style.util.SelectorPredicate;
import com.github.s262316.forx.tree.style.util.Selectors;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.EnumSet;

public class LinkElementHandler extends XMutationListener implements LinkingMechanism
{
    private final static Logger logger= LoggerFactory.getLogger(LinkElementHandler.class);

    private XmlVElement linkElement;
    private Stylesheet linkElementStylesheet;
    private boolean connected=false;
    private Charset charset=null;

    public LinkElementHandler(XmlVElement linkElement)
    {
        super(new SelectorPredicate(Selectors.createSimpleElementNameSelector("link")),
                PropagationType.CAPTURE, linkElement, EnumSet.of(MutationType.ADD, MutationType.CONNECT, MutationType.DISCONNECT));

        this.linkElement=linkElement;
    }

    // connected is invoked for subject and children of subject
    //
    @Override
    public void connected(XmlMutationEvent event)
    {
        logger.debug("connected listener on={}, subject={}", this.getListenee(), event.getSubject());

        // only merge stylesheet if we have one
        if(linkElementStylesheet!=null)
        {
            Preconditions.checkArgument(connected==false);

            linkElement.getDocument().mergeStyles(linkElement, linkElementStylesheet);
            connected=true;
        }
    }

    // disconnected is invoked for subject and children of subject
    @Override
    public void disconnected(XmlMutationEvent event)
    {
        logger.debug("disconnected listener on={}, subject={}", this.getListenee(), event.getSubject());

        // only demerge if style we have one
        if(linkElementStylesheet!=null)
        {
            Preconditions.checkArgument(connected==true);

            linkElement.getDocument().demergeStylesFrom(linkElement);

            linkElementStylesheet=null;
            connected=false;
        }
    }

    @Override
    public void removed(XmlMutationEvent event)
    {

    }

    // added is invoked for everything down to the subject
    // let's not worry about adding nodes within <link>
    @Override
    public void added(XmlMutationEvent event)
    {
        try
        {
            logger.debug("added listener on={}, subject={}", this.getListenee(), event.getSubject());

            String rel=XNodes.getAttributeValue(linkElement, "rel", "stylesheet");
            if (rel.equalsIgnoreCase("stylesheet"))
            {
                String href=XNodes.getAttributeValue(linkElement, "href").orNull();
                if (href != null)
                {
                    charset = XNodes.getAttributeValue(linkElement, "charset")
                            .transform(Charset::forName).orNull();

                    CssLoader cssLoader = new CssLoader(this);
                    CSSParser cssParser = new CSSParser(href, (XmlDocument) linkElement.getDocument(), cssLoader);
                    linkElementStylesheet = cssParser.parse_stylesheet();

                    if (connected)
                    {
                        linkElement.getDocument().mergeStyles(linkElement, linkElementStylesheet);
                    }
                }
            }
        }
        catch(IOException | CSSParserException e)
        {
            logger.error("cannot process stylesheet", e);
        }
    }

    @Override
    public Optional<Charset> getCharset()
    {
        return Optional.fromNullable(charset);
    }
}
