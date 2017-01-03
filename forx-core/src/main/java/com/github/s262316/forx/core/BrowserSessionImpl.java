package com.github.s262316.forx.core;

import java.awt.Graphics2D;
import java.io.*;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.github.s262316.forx.box.RootBox;
import com.github.s262316.forx.common.ApplicationConfigException;
import com.github.s262316.forx.core.diagn.BoxDrivenDiagnostics;
import com.github.s262316.forx.core.real.BoxDrawer;
import com.github.s262316.forx.core.real.BoxRealMapping;
import com.github.s262316.forx.core.real.RealMouseMoveEvent;
import com.github.s262316.forx.core.real.RealMousePressedEvent;
import com.github.s262316.forx.graphics.GraphicsContext;
import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.tree.impl.XmlDocument;
import com.github.s262316.forx.tree.impl.XmlNode;
import com.github.s262316.forx.tree.visual.XmlVElement;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

@Component
public class BrowserSessionImpl implements WebpageHolder
{
	private final static Logger logger=LoggerFactory.getLogger(BrowserSessionImpl.class);	

    private XmlDocument doc;
	@Autowired
	ApplicationContext applicationContext;
	@Autowired
    private BoxRealMapping screenTranslator;
    private CloseableHttpClient httpClient=HttpClients.createDefault();
    private MouseEventProcessor mouseEventProcessor;
    private EventDispatcher eventDispatcher=new EventDispatcher();
	@Autowired
    private BoxDrawer drawer;
    
//    @Autowired
    public BrowserSessionImpl()//BoxRealMapping realMapping, BoxDrawer drawer)
    {
		logger.debug("ctor");
    	
//        this.graphicsContext=graphicsContext;
//        this.screenTranslator=realMapping;
//        this.drawer=drawer;
        
        mouseEventProcessor=new MouseEventProcessor(eventDispatcher);
    }

    public void open(String location) throws IOException, ApplicationConfigException
    {
    	try
    	{
//    	HttpGet getRequest=new HttpGet(location);
//    	
//    	CloseableHttpResponse response=httpClient.execute(getRequest);
//    	
//    	HttpEntity ent=response.getEntity();  	
//    	InputStream is=ent.getContent();
//    	char ch=(char)is.read();
//    	while(ch!=-1)
//    	{
//    		System.out.print(ch);
//    		ch=(char)is.read();
//
		logger.debug("open("+location+")");
		URL locationUrl=new URL(location);
		InputStream inputStream=locationUrl.openStream();

		GraphicsContext graphicsContext=applicationContext.getBean(GraphicsContext.class);

        XmlVDocumentBuilder docBuilder=new XmlVDocumentBuilder(graphicsContext, eventDispatcher, screenTranslator);
        StaxBinding binding=new StaxBinding(eventDispatcher, docBuilder, locationUrl);
        
        SAXParserFactory spf=SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(true);
        SAXParser sp=spf.newSAXParser();


        binding.parse(inputStream);
        
        //Html33Binding binding=new Html33Binding(eventDispatcher, docBuilder);
        //new ParserDelegator().parse(in, binding, false);

        
        
		doc=docBuilder.getDoc();
		inputStream.close();
		
		
    	}
    	catch(SAXException saxe)
    	{
    		logger.error("", saxe);
    		throw new RuntimeException(saxe);
    	}
    	catch(XMLStreamException xse)
    	{
    		logger.error("", xse);
    		throw new RuntimeException(xse);
    	}    	
    	catch(ParserConfigurationException pce)
    	{
    		logger.error("", pce);
    		throw new RuntimeException(pce);
    		
    	}

		
    }

    public void redraw(Graphics2D graphics)
    {
		screenTranslator.invalidateRealPositions();
		
		XmlVElement root=(XmlVElement)doc.getRoot();
		RootBox box=(RootBox)root.visualBox();
		drawer.draw_tree(box, 0, 0, graphics);
		
		BoxDrivenDiagnostics.output(box);
    }
    
	@Override
	public XmlDocument getDocument()
	{
		return doc;
	}
    
	@Subscribe
	public void handleMouseMove(RealMouseMoveEvent event)
	{
		List<XmlNode> nodesHit;
		
		nodesHit=screenTranslator.componentsAtPoint(event.getX(), event.getY());
		
		mouseEventProcessor.process(Sets.newLinkedHashSet(nodesHit), event.getX(), event.getY());
	}
	
	@Subscribe
	public void handleMousePress(RealMousePressedEvent event)
	{
		List<XmlNode> nodesHit;
		
		nodesHit=screenTranslator.componentsAtPoint(event.getX(), event.getY());

		mouseEventProcessor.processPress(Sets.newLinkedHashSet(nodesHit), event.getX(), event.getY());
	}
}



