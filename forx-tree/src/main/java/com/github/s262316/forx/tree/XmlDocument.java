package com.github.s262316.forx.tree;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;
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

public class XmlDocument extends XmlNode implements XDocument, ReferringDocument
{
    private DocumentTypeDecl documentType;
    private XElement root;
    private List<XmlNode> members=new LinkedList<XmlNode>();
    private List<XmlElement> allElements=new LinkedList<XmlElement>();
    private List<XAttribute> allAttrs=new LinkedList<XAttribute>();
    private List<XComment> allComments=new LinkedList<XComment>();
    private List<XmlText> allTexts=new LinkedList<XmlText>();
    private XmlDocumentBuilder builder;
    private boolean doc_complete;
    private EventDispatcher eventDispatcher;
    private URL location;

    public XmlDocument(DocumentTypeDecl dtd, XmlDocumentBuilder builder, EventDispatcher eventDispatcher, URL location)
    {
        super(null, 0);

        this.documentType=dtd;
        this.root=null;
        this.builder=builder;
        this.eventDispatcher=eventDispatcher;
        this.location=location;

        setDocument(this);

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

    @Override
    public Optional<Charset> getCharset()
    {
        return Optional.of(StandardCharsets.UTF_8);
    }
}

