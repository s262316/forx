package com.github.s262316.forx.gui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.core.BrowserSessionImpl;
import com.github.s262316.forx.core.real.RealMouseMoveEvent;
import com.github.s262316.forx.core.real.RealMousePressedEvent;
import com.github.s262316.forx.graphics.GraphicsContext;

import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebView extends JComponent implements GraphicsContext, MouseMotionListener, MouseListener
{
	final static Logger logger=LoggerFactory.getLogger(WebView.class);
	private BrowserSessionImpl session;

	private Graphics tempGraphics=null;
	public String tempLocationMoveMe;
	private EventBus viewEventBus=new EventBus("viewEventBus");
	private DrawListener drawListener;

	@Autowired
    public WebView(BrowserSessionImpl browserSession)
    {
		logger.debug("ctor");
		this.session=browserSession;
    	
//    	this.addMouseMotionListener(this);
    	this.addMouseListener(this);
    	viewEventBus.register(session);
    }
    
    public void load(String location)
    {
    	tempLocationMoveMe=location;
   		repaint();
    }
    
    @Override
	public void paint(Graphics g)
	{
    	try
    	{
//        	CdiContainer cdiContainer = CdiContainerLoader.getCdiContainer();
//            cdiContainer.boot();
//            
//            ContextControl contextControl = cdiContainer.getContextControl();
//            contextControl.startContext(ApplicationScoped.class);        
    		
        	tempGraphics=g;

    		session.open(tempLocationMoveMe);
    		session.redraw((Graphics2D)g);
    		
//    		cdiContainer.shutdown();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
		if(drawListener!=null)
			drawListener.finishedDrawing();
	}
    
    @Override
	public int getDpi()
    {
        return Toolkit.getDefaultToolkit().getScreenResolution();
    }

    @Override
	public FontMetrics fontMetrics(Font font)
    {
    	return super.getFontMetrics(font);
	}

	@Override
    public Rectangle get_browser_area_limits()
    {
        return this.getBounds();
    }

    @Override
    public void setContentHeight(int nh)
    {
//        throw new RuntimeException("Not implemented yet");

    }

    @Override
    public void setContentWidth(int nw)
    {
//        throw new RuntimeException("Not implemented yet");

    }

	@Override
	public void mouseDragged(MouseEvent e)
	{
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
//		logger.debug("mouseMoved {}", e);
		viewEventBus.post(new RealMouseMoveEvent(e.getX(), e.getY()));
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		System.out.println("mousePressed");
		viewEventBus.post(new RealMousePressedEvent(e.getX(), e.getY()));
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
//		viewEventBus.post(new RealMousePressedEvent(e.getX(), e.getY()));
	}
	
	public void setDrawListener(DrawListener drawListener)
	{
		this.drawListener=drawListener;
	}
}

