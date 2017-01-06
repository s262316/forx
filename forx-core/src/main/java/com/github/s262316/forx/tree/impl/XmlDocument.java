package com.github.s262316.forx.tree.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import org.apache.commons.io.FileUtils;
import com.github.s262316.forx.common.ApplicationConfigException;
import com.github.s262316.forx.css.CSSParser;
import com.github.s262316.forx.css.CSSParserException;
import com.github.s262316.forx.css.CssLoader;
import com.github.s262316.forx.tree.*;
import com.github.s262316.forx.tree.build.AttributeKey;
import com.github.s262316.forx.tree.build.CommentKey;
import com.github.s262316.forx.tree.build.ElementKey;
import com.github.s262316.forx.tree.build.TextKey;
import com.github.s262316.forx.tree.build.XmlDocumentBuilder;
import com.github.s262316.forx.tree.events2.Event;
import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.tree.events2.Listener;
import com.github.s262316.forx.tree.events2.MutationType;
import com.github.s262316.forx.tree.events2.PropagationType;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;
import com.github.s262316.forx.tree.style.Declaration;
import com.github.s262316.forx.tree.style.MediaType;
import com.github.s262316.forx.tree.style.StyleRule;
import com.github.s262316.forx.tree.style.Stylesheet;
import com.github.s262316.forx.tree.style.StylesheetFactory;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.selectors.PseudoClassType;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.style.util.Selectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import org.springframework.util.ResourceUtils;

public class XmlDocument extends XmlNode implements XDocument, ReferringDocument
{
    private DocumentTypeDecl documentType;
    private XElement root;
    private List<XmlNode> members=new LinkedList<XmlNode>();
    private List<XmlElement> allElements=new LinkedList<XmlElement>();
    private List<XAttribute> allAttrs=new LinkedList<XAttribute>();
    private List<XComment> allComments=new LinkedList<XComment>();
    private List<XmlText> allTexts=new LinkedList<XmlText>();
    private Stylesheet agentStylesheet;
    private Stylesheet userNormalStylesheet;
    private Stylesheet authorNormalStylesheet;
    private Stylesheet authorImportantStylesheet;
    private Stylesheet userImportantStylesheet;
    private ListMultimap<XmlNode, Stylesheet> styleSource = ArrayListMultimap.create();
    private XmlDocumentBuilder builder;
    private boolean doc_complete;
    private String defaultStyleLanguage="text/css";
    private EventDispatcher eventDispatcher;
    private CssLoader cssLoader;
    private URL location;

    public XmlDocument(DocumentTypeDecl dtd, XmlDocumentBuilder builder, EventDispatcher eventDispatcher, URL location) throws ApplicationConfigException
    {
        super(null, 0);

        this.documentType=dtd;
        this.root=null;
        this.builder=builder;
        this.eventDispatcher=eventDispatcher;
        this.location=location;

        userNormalStylesheet=StylesheetFactory.createEmptyStylesheet();
        authorNormalStylesheet=StylesheetFactory.createEmptyStylesheet();
        authorImportantStylesheet=StylesheetFactory.createEmptyStylesheet();
        userImportantStylesheet=StylesheetFactory.createEmptyStylesheet();

        setDocument(this);

        cssLoader=new CssLoader(() -> com.google.common.base.Optional.absent());

        try
        {
        	// TODO difference css for xhtml/html docs
            File userAgentCss=ResourceUtils.getFile("classpath:com/github/s262316/forx/css/user_agent.css");
            String cssData=FileUtils.readFileToString(userAgentCss, StandardCharsets.UTF_8);

            CSSParser parser=new CSSParser(cssData, this);
            agentStylesheet=parser.parse_stylesheet();
        }
        catch(CSSParserException cpe)
        {
            throw new ApplicationConfigException(cpe);
        }
        catch(MalformedURLException mue)
        {
            throw new ApplicationConfigException(mue);
        }
        catch(IOException ioe)
        {
            throw new ApplicationConfigException(ioe);
		}
    }
    
    public void mergeStyles(XmlNode source, Stylesheet ss)
    {
       	styleSource.put(source, ss);
       	refreshStyles();
    }

    public void demergeStylesFrom(XmlNode source)
    {
        styleSource.removeAll(source);
       	refreshStyles();
    }

    private void refreshStyles()
    {
    	authorNormalStylesheet.reset();
    	authorImportantStylesheet.reset();
    	
    	for(Map.Entry<XmlNode, Stylesheet> e : styleSource.entries())
    	{
    		authorNormalStylesheet.mergeNormalRules(e.getValue());
    		authorImportantStylesheet.mergeImportantRules(e.getValue());
    	}
    }
    
    public DocumentTypeDecl getDocumentType()
    {
        return documentType;
    }

    @Override
    public boolean full()
    {
        return doc_complete;
    }

    @Override
    public void complete(boolean full)
    {
        doc_complete=full;
    }

    @Override
    public NodeType type()
    {
        return NodeType.X_DOCUMENT;
    }

    @Override
    public String getName()
    {
        return "";
    }

    @Override
    public String getValue()
    {
        return "";
    }

    @Override
    public XElement getRoot()
    {
        return root;
    }

    public void setDefaultStyleLanguage(String language)
    {
        defaultStyleLanguage=language;
    }

    public String getDefaultStyleLanguage()
    {
        return defaultStyleLanguage;
    }

    @Override
    public XElement findElementById(String id)
    {
        XElement e, retval=null;
        XAttribute a;
        boolean found=false;

        Iterator<XmlElement> it=allElements.iterator();
        while(it.hasNext()&&found==false)
        {
            e=it.next();

            if(e.type()==NodeType.X_ELEMENT)
            {
                a=e.getAttr("id");
                if(a!=null)
                {
                    if(a.getValue().equals(id))
                    {
                        found=true;
                        retval=e;
                    }
                }
            }
        }

        return retval;
    }

    public XmlElement createElement(ElementKey key)
    {
        XmlElement e;

        e=builder.createElement(key);
        allElements.add(e);

        return e;
    }

    @Override
    public URL getLocation()
    {
        return location;
    }

    public XmlAttribute createAttribute(AttributeKey key)
    {
        XmlAttribute a;

        a=builder.createAttr(key);
        allAttrs.add(a);

        return a;
    }

    public XmlComment createComment(CommentKey key)
    {
        XmlComment c;

        c=builder.createComment(key);
        allComments.add(c);

        return c;
    }

    public XmlText createText(TextKey key)
    {
        XmlText t;

        t=builder.createText(key);
        allTexts.add(t);

        return t;
    }

    public Value getPropertyValue(XmlElement element, String propertyName, MediaType mediaType, PseudoElementType pseudoType)
    {
        Declaration d;
        Value v=null;

        d=userImportantStylesheet.findDeclaration(element, propertyName, mediaType, pseudoType);
        if(d==null)
        {
            d=authorImportantStylesheet.findDeclaration(element, propertyName, mediaType, pseudoType);
            if(d==null)
            {
                d=authorNormalStylesheet.findDeclaration(element, propertyName, mediaType, pseudoType);
                if(d==null)
                {
                    d=userNormalStylesheet.findDeclaration(element, propertyName, mediaType, pseudoType);
                    if(d==null)
                    {
                        d=agentStylesheet.findDeclaration(element, propertyName, mediaType, pseudoType);
                    }
                }
            }
        }

        if(d!=null)
            v=d.getValue();

        return v;
    }

	public boolean isSensitiveToPseudoElement(XmlElement element, MediaType mediaType, PseudoElementType pseudoType, PseudoClassType pseudoClass)
	{
		List<StyleRule> rules;
		
    	rules=userImportantStylesheet.findAllRules(element, mediaType, pseudoType);
        if(rules.isEmpty())
        {
        	rules=authorImportantStylesheet.findAllRules(element, mediaType, pseudoType);
            if(rules.isEmpty())
            {
            	rules=authorNormalStylesheet.findAllRules(element, mediaType, pseudoType);
                if(rules.isEmpty())
                {
                	rules=userNormalStylesheet.findAllRules(element, mediaType, pseudoType);
                    if(rules.isEmpty())
                    {
                    	rules=agentStylesheet.findAllRules(element, mediaType, pseudoType);
                    }
                }
            }
        }
        
        return !rules.isEmpty();
	}    
    
    public boolean isSensitiveToPseudoClass(XmlElement element, MediaType mediaType, PseudoElementType pseudoType, PseudoClassType pseudoClass)
    {
    	Collection<StyleRule> allRules=Lists.newArrayList();
    	Collection<StyleRule> rules;

    	Preconditions.checkArgument(pseudoClass!=PseudoClassType.PCT_NO_CLASS, "isSensitiveTo not appropriate with PCT_NO_CLASS");
    	Preconditions.checkArgument(pseudoClass!=PseudoClassType.PCT_FIRST_CHILD, "isSensitiveTo not appropriate with PCT_FIRST_CHILD");
    	Preconditions.checkArgument(pseudoClass!=PseudoClassType.PCT_LANG, "isSensitiveTo not appropriate with PCT_LANG");
    	
    	rules=userImportantStylesheet.findAllRules(element, mediaType, pseudoType);
    	allRules.addAll(rules);
       	rules=authorImportantStylesheet.findAllRules(element, mediaType, pseudoType);
    	allRules.addAll(rules);
       	rules=authorNormalStylesheet.findAllRules(element, mediaType, pseudoType);
    	allRules.addAll(rules);
       	rules=userNormalStylesheet.findAllRules(element, mediaType, pseudoType);
    	allRules.addAll(rules);
       	rules=agentStylesheet.findAllRules(element, mediaType, pseudoType);
    	allRules.addAll(rules);

    	for(StyleRule sr : allRules)
    	{
    		if(Selectors.containsPseudoType(sr.getSelector())==true)
    			return true;
    	}
    	
       	return false;
    }
    

    public Value getPropertyValue(XmlElement element, String propertyName, MediaType mediaType)
    {
        return getPropertyValue(element, propertyName, mediaType, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public void dispatchEvent(XmlNode target, Event event)
    {
    	eventDispatcher.fire(target, event);
    }
    
    @Deprecated
	public TreePath pathToHere(XNode target)
	{
		int parts=1, i;
		XNode parent=target;

		while(parent.parentNode()!=null)
		{
			if(parent.parentNode().type()!=NodeType.X_DOCUMENT)
				parts++;
			parent=parent.parentNode();
		}

		XNode path[]=new XNode[parts];
		parent=target;
		for(i=parts-1; i>=0; i--)
		{
			path[i]=parent;
			parent=parent.parentNode();
		}

		return new TreePath(path, parts);
	}
    
    public void add(XmlNode n) throws TreeException
    {
        if(n.type()==NodeType.X_ELEMENT)
        {
            if(root!=null)
                throw new TreeException(TreeExceptionType.ALREADY_A_ROOT_ELEMENT); // already a root element

            root=(XmlElement) n;
        }

        XmlNode node=n;
        XmlMutationEvent event=new XmlMutationEvent(MutationType.ADD, node);

        node.setParentNode(this);
        members.add(node);
        
        // (1) fire MUTATION/ADD to node
        dispatchEvent(node, event);

        // (2) fire CONNECT to node's attributes
        // (3) fire CONNECT to node's children
        if(n.type()==NodeType.X_ELEMENT)
        {
            XmlElement e=(XmlElement) n;
        	XmlMutationEvent connectEvent=new XmlMutationEvent(MutationType.CONNECT, node);

        	eventDispatcher.broadcast(node, connectEvent);
        }
    }

    public void remove(XmlNode node)
    {
        if(!members.contains(node))
            throw new RuntimeException();

        XmlMutationEvent event=new XmlMutationEvent(MutationType.REMOVE, node);
        dispatchEvent(node, event);

        if(node.type()==NodeType.X_ELEMENT)
        {
            XmlElement e=(XmlElement) node;
        	XmlMutationEvent connectEvent=new XmlMutationEvent(MutationType.DISCONNECT, node);

        	eventDispatcher.broadcast(node, connectEvent);
        }

        members.remove(node);
        if(node.type()==NodeType.X_ELEMENT)
            root=null;
    }

    public void insert(XmlNode newNode, XmlNode beforeNode)
    {
    }

    @Override
    public <T extends Event> void addListener(XmlNode listenee, PropagationType propagation, Class<T> eventType, Listener<? extends Event> listener)
    {
    	eventDispatcher.addListener(listenee, propagation, eventType, listener);
    }

    public CssLoader getCssLoader()
    {
        return cssLoader;
    }

    @Override
    public Optional<Charset> getCharset()
    {
        return Optional.of(StandardCharsets.UTF_8);
    }
}

