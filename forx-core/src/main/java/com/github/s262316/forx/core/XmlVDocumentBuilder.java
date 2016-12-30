package com.github.s262316.forx.core;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.common.ApplicationConfigException;
import com.github.s262316.forx.core.real.BoxRealMapping;
import com.github.s262316.forx.graphics.GraphicsContext;
import com.github.s262316.forx.tree.build.AttributeKey;
import com.github.s262316.forx.tree.build.CommentKey;
import com.github.s262316.forx.tree.build.ElementKey;
import com.github.s262316.forx.tree.build.TextKey;
import com.github.s262316.forx.tree.build.XmlDocumentBuilder;
import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.tree.impl.XmlAttribute;
import com.github.s262316.forx.tree.impl.XmlComment;
import com.github.s262316.forx.tree.impl.XmlDocument;
import com.github.s262316.forx.tree.impl.XmlElement;
import com.github.s262316.forx.tree.impl.XmlText;
import com.github.s262316.forx.tree.visual.BoxBuilder;
import com.github.s262316.forx.tree.visual.MetaTagHandler;
import com.github.s262316.forx.tree.visual.StyleAttributeHandler;
import com.github.s262316.forx.tree.visual.StyleElementHandler;
import com.github.s262316.forx.tree.visual.TextBuilder;
import com.github.s262316.forx.tree.visual.VisualConstants;
import com.github.s262316.forx.tree.visual.VisualState;
import com.github.s262316.forx.tree.visual.XmlVElement;
import com.github.s262316.forx.tree.visual.XmlVText;


public class XmlVDocumentBuilder implements XmlDocumentBuilder
{
	private final static Logger logger=LoggerFactory.getLogger(XmlVDocumentBuilder.class);
	
    private GraphicsContext graphicsContext;
    private int id=0;
    private XmlDocument doc;
    private BoxRealMapping boxRealMapping;
    private EventDispatcher eventDispatcher;

    public XmlVDocumentBuilder(GraphicsContext graphicsContext, EventDispatcher eventDispatcher, BoxRealMapping boxRealMapping) throws ApplicationConfigException
    {
        this.graphicsContext=graphicsContext;
        this.boxRealMapping=boxRealMapping;
        this.eventDispatcher=eventDispatcher;
    }

    @Override    
    public void setDoc(XmlDocument doc)
    {
    	this.doc=doc;
    }
    
    public XmlDocument getDoc()
    {
		return doc;
	}

    @Override
    public XmlElement createElement(ElementKey key)
    {
        XmlVElement e;
        AttributeKey ak;
        XmlAttribute a;

		e=new XmlVElement(key.name, doc, nextId(), graphicsContext, eventDispatcher);

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
			e.addMutationListener(new StyleElementHandler(e));
		e.addMutationListener(new StyleAttributeHandler(e));
		e.addMutationListener(new BoxBuilder(e, this.boxRealMapping));
		e.addMutationListener(new MetaTagHandler(e));
		
		e.setProperty(VisualConstants.VISUAL_STATE, new VisualState());
		
		this.
		
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
