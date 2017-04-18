package com.github.s262316.forx.tree.visual;


/**
 * creates stylesheets for
 * (a) style attributes
 * (b) style elements
 * (c) link elements with stylesheets
 *
 */
public class ComputeStylesheetListener //extends XMutationListener
{
//	private final static Logger logger=LoggerFactory.getLogger(ComputeStylesheetListener.class);
//
//	private XmlVElement listenee;
//	
//	public ComputeStylesheetListener(XmlVElement listenee)
//	{
//		super(PropagationType.CAPTURE, listenee, EnumSet.allOf(MutationType.class));
//		this.listenee=listenee;
//	}
//
//	@Override
//	public void added(XmlMutationEvent event)
//	{
//		logger.debug("eventAdd event={} listenee={}", event, listenee);
//
//		if(listenee==event.getSubject())
//		{
//			XmlDocument d=(XmlDocument)listenee.getDocument();
//			// look on style attribute
//			XAttribute a=listenee.getAttr("style");
//			if(a!=null)
//				processElementStyles(a);
//
//			if(listenee.getName().equals("link"))
//			{
//				XAttribute a2=listenee.getAttr("href");
//				if(a2!=null)
//				{
//					try
//					{
//						URL file=new URL(d.getLocation(), a2.getValue());
//
//						Stylesheet ss=CSSParser.parseFromStream(new InputStreamReader(file.openStream()));
//						ss.setOrder(listenee.getId());
//						if(d.getDefaultStyleLanguage().equals(""))
//							d.setDefaultStyleLanguage("text/com.github.s262316.forx.css");
//
//						d.mergeStyles(listenee, ss);
//					}
//					catch(MalformedURLException mue)
//					{
//					}
//					catch(IOException ioe)
//					{
//					}
//				}
//			}
////			else if(listenee.getName().equals("img"))
////			{
////				try
////				{
////					URL file=new URL(d.getLocation(), listenee.getAttr("src").getValue());
////
////					listenee.set_plugin_handler(new ImagePlugin(file.toString()));
////				}
////				catch(MalformedURLException mue)
////				{
////				}
////			}
//
//		}
//		else
//		{
//			if(listenee.getName().equals("style"))
//			{
//				// is the target "connected"?
//				if(event.getSubject().isConnected() && event.getSubject().type()==NodeType.X_TEXT)
//				{
//					unprocessElementStyles(listenee);
//					processElementStyles(listenee);
//				}
//			}
//		}
//	}
//
//	@Override
//	public void removed(XmlMutationEvent event)
//	{
//		if(listenee!=event.getSubject())
//		{
//			unprocessElementStyles(listenee);
//		}
//	}
//
//	@Override
//	public void connected(XmlMutationEvent event)
//	{
//		// an element with a style attribute should already be processed
//		// are we connecting a style element?
//		if(listenee!=event.getSubject())
//		{
//			if(listenee.getName().equals("style"))
//			{
//				processElementStyles(listenee);
//			}
//		}
//	}
//
//	@Override
//	public void disconnected(XmlMutationEvent event)
//	{
//		// are we disconnecting an element with a style attribute?
//		if(listenee!=event.getSubject())
//		{
//			if(listenee.getName().equals("style"))
//			{
//				unprocessElementStyles(listenee);
//			}
//		}
//	}
//
//	private void unprocessElementStyles(XmlVElement e)
//	{
//		XAttribute a=e.getAttr("style");
//		if(a!=null)
//			e.clearStyles();
//		else if(e.getName().equals("style"))
//			((XmlDocument)e.getDocument()).demergeStyles(e);
//	}
//
//	private void processElementStyles(XmlVElement e)
//	{
//		try
//		{
//			String text=collectAllText(e);
//			XAttribute a;
//
//			a=e.getAttr("type");
//			if(a!=null && ((XmlDocument)e.getDocument()).getDefaultStyleLanguage().equals(""))
//				((XmlDocument)e.getDocument()).setDefaultStyleLanguage(a.getValue());
//			Stylesheet ss=CSSParser.parseFromStream(new StringReader(text));
//
//			ss.setOrder(e.getId());
//			((XmlDocument)e.getDocument()).mergeStyles(e, ss);
//		}
//		catch(IOException ioe)
//		{
//		}
//	}
//
//	private void processElementStyles(XAttribute a)
//	{
//		logger.debug("processElementStyles");
//
//		try
//		{
//			List<Declaration> decs=CSSParser.parseFromDeclistStream(a.getValue());
//			//ss.setOrder(a.getId());
//
//			logger.debug("size "+decs.size());
//			logger.debug("decs(0) "+decs.get(0));
//
//			((XmlVElement)a.parentNode()).setStyles(decs);
//		}
//		catch(IOException ioe)
//		{
//		}
//	}
//
//	private static String collectAllText(XElement e)
//	{
//		String str="";
//
//		for(XNode xn : e.getMembers())
//		{
//			str+=xn.getValue();
//		}
//
//		return str;
//	}
}

