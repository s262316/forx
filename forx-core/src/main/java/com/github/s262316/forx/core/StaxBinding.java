package com.github.s262316.forx.core;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.XMLInputFactory2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.common.ApplicationConfigException;
import com.github.s262316.forx.tree.TreeException;
import com.github.s262316.forx.tree.build.AttributeKey;
import com.github.s262316.forx.tree.build.ElementKey;
import com.github.s262316.forx.tree.build.TextKey;
import com.github.s262316.forx.tree.build.XmlDocumentBuilder;
import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.tree.impl.XmlDocument;
import com.github.s262316.forx.tree.impl.XmlNode;
import com.github.s262316.forx.tree.visual.VisualConstants;
import com.github.s262316.forx.tree.visual.XmlVElement;


public class StaxBinding
{
	private final static Logger logger=LoggerFactory.getLogger(StaxBinding.class);
	
    private XmlDocument doc;
    private XmlVElement root=null;
    private XmlVElement current=null;
    private XmlDocumentBuilder builder;
    private EventDispatcher eventDispatcher;
	private URL location;
		
    public StaxBinding(EventDispatcher eventDispatcher, XmlDocumentBuilder builder, URL location)
    {
        this.builder=builder;
        this.eventDispatcher=eventDispatcher;
		this.location=location;
    }        
    
    public void parse(InputStream in) throws XMLStreamException
	{
		XMLInputFactory2 xmlInputFactory = (XMLInputFactory2)XMLInputFactory.newInstance();
		xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
		xmlInputFactory.setXMLResolver(new XHTMLResourceResolver());

		XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(in);

		while(xmlEventReader.hasNext())
		{
			XMLEvent xmlEvent = xmlEventReader.nextEvent();
			
			if(xmlEvent.isStartDocument())
			{
				logger.debug("startDocument");
				
				try
				{
					doc=new XmlDocument(null, builder, eventDispatcher, location);
					doc.setProperty(VisualConstants.META_TAGS, new HashMap<String, String>());
					builder.setDoc(doc);
				}
				catch(ApplicationConfigException ace)
				{
					throw new RuntimeException(ace);
				}				
			}
			else if(xmlEvent.isStartElement())
			{
				try
				{
			    	logger.debug("startElement element={}", xmlEvent.asStartElement());

					ElementKey ek=new ElementKey();
					ek.name=xmlEvent.asStartElement().getName().getLocalPart();
					ek.members=new ArrayList<XmlNode>();
					ek.attrs=new HashMap<String, AttributeKey>();
					ek.whitespaceIsSignificant=false;
					ek.lang="en-GB";
					
					AttributeKey ak;
					Object obj;
					
					Iterator ait=xmlEvent.asStartElement().getAttributes();
					while(ait.hasNext())
					{
						Attribute a=(Attribute)ait.next();
						
						logger.debug("attr {}={}", a.getName().getLocalPart(), a.getValue());

						ak=new AttributeKey();
						ak.name=a.getName().getLocalPart();
						ak.value=a.getValue();

						ek.attrs.put(a.getName().getLocalPart(), ak);
					}

					XmlVElement ve=(XmlVElement)doc.createElement(ek);

					if(root==null)
					{
						root=ve;
						doc.add(ve);
					}
					else
						current.add(ve);

					current=ve;

				}
				catch(TreeException e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}				
			}
			else if(xmlEvent.isEndElement())
			{
		    	logger.debug("endElement name={}", current, xmlEvent.asEndElement().toString());
		    	
				current.complete(false);
				
				current=current.visualParentNode();				
			}
			else if(xmlEvent.isEndDocument())
			{
				
			}
			else if(xmlEvent.isCharacters())
			{
				try
				{
					logger.debug("handleText \""+xmlEvent.asCharacters().getData()+"\"");

					TextKey tk=new TextKey();
					tk.text=xmlEvent.asCharacters().getData();
					current.add(doc.createText(tk));
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}					
			}
		}
	}
}
