package com.github.s262316.forx.core;

import java.net.URL;
import java.util.Map;

import com.github.s262316.forx.css.CSSPropertiesReference;
import com.github.s262316.forx.css.VisualConstants;
import com.github.s262316.forx.css.VisualState;
import com.github.s262316.forx.tree.DocumentTypeDecl;
import com.github.s262316.forx.tree.XmlAttribute;
import com.github.s262316.forx.tree.XmlComment;
import com.github.s262316.forx.tree.XmlElement;
import com.github.s262316.forx.tree.XmlText;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.adders.*;
import com.github.s262316.forx.common.ApplicationConfigException;
import com.github.s262316.forx.core.real.BoxRealMapping;
import com.github.s262316.forx.graphics.GraphicsContext;
import com.github.s262316.forx.tree.build.AttributeKey;
import com.github.s262316.forx.tree.build.CommentKey;
import com.github.s262316.forx.tree.build.ElementKey;
import com.github.s262316.forx.tree.build.TextKey;
import com.github.s262316.forx.tree.build.XmlDocumentBuilder;
import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.tree.visual.BoxBuilder;
import com.github.s262316.forx.tree.visual.LinkElementHandler;
import com.github.s262316.forx.tree.visual.MetaTagHandler;
import com.github.s262316.forx.tree.visual.StyleAttributeHandler;
import com.github.s262316.forx.tree.visual.StyleElementHandler;
import com.github.s262316.forx.tree.visual.TextBuilder;
import com.github.s262316.forx.tree.visual.XmlVDocument;
import com.github.s262316.forx.tree.visual.XmlVElement;
import com.github.s262316.forx.tree.visual.XmlVText;

public class XmlVDocumentBuilder implements XmlDocumentBuilder
{
	private final static Logger logger=LoggerFactory.getLogger(XmlVDocumentBuilder.class);
	
    private GraphicsContext graphicsContext;
    private int id=0;
    private XmlVDocument doc;
    private BoxRealMapping boxRealMapping;
    private EventDispatcher eventDispatcher;
    private CSSPropertiesReference cssPropertiesReference;

    public XmlVDocumentBuilder(GraphicsContext graphicsContext, EventDispatcher eventDispatcher, BoxRealMapping boxRealMapping, CSSPropertiesReference cssPropertiesReference) throws ApplicationConfigException
    {
        this.graphicsContext=graphicsContext;
        this.boxRealMapping=boxRealMapping;
        this.eventDispatcher=eventDispatcher;
        this.cssPropertiesReference=cssPropertiesReference;
    }

	public XmlVDocument createDocument(URL location)
    {
        try
        {
            doc = new XmlVDocument(new DocumentTypeDecl(), this, eventDispatcher, location, cssPropertiesReference);
            return doc;
        }
        catch (ApplicationConfigException ace)
        {
            throw new RuntimeException(ace);
        }
    }

    @Override
    public XmlElement createElement(ElementKey key)
    {
        XmlVElement e;
        AttributeKey ak;
        XmlAttribute a;

		e=new XmlVElement(key.name, doc, nextId(), graphicsContext, eventDispatcher, cssPropertiesReference);

		for(Map.Entry<String, AttributeKey> it : key.attrs.entrySet())
		{
			ak=it.getValue();
			a=createAttr(ak);
			a.setParentNode(e);
			e.attrs.put(it.getKey(), a);
		}

		e.members.addAll(key.members);
		e.language=key.lang;

		if(key.name.equals("style"))
			e.addMutationListener(new StyleElementHandler(e, cssPropertiesReference));
        else if(key.name.equals("link"))
            e.addMutationListener(new LinkElementHandler(e, cssPropertiesReference));

		e.addMutationListener(new StyleAttributeHandler(e, cssPropertiesReference));
		e.addMutationListener(new BoxBuilder(e, this.boxRealMapping));
		e.addMutationListener(new MetaTagHandler(e));
		
		e.setProperty(VisualConstants.VISUAL_STATE, new VisualState());
		
		logger.debug("created ("+key.name+") "+e);

        return e;
    }

    @Override
    public XmlAttribute createAttr(AttributeKey key)
    {
        return new XmlAttribute(key.name, key.value, doc, nextId());
    }

    @Override
    public XmlText createText(TextKey key)
    {
        XmlVText t;

        t=new XmlVText(key.text, doc, nextId(), graphicsContext);
        t.addMutationListener(new TextBuilder(t));

		logger.debug("created ("+key.text+") "+t);

        return t;
    }

    @Override
    public XmlComment createComment(CommentKey key)
    {
        return new XmlComment(key.text, doc, nextId());
    }

    private int nextId()
    {
    	return ++id;
    }

}
