/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.tree.visual;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.tree.events2.MutationType;
import com.github.s262316.forx.tree.events2.PropagationType;
import com.github.s262316.forx.tree.events2.XMutationListener;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;

public class TextBuilder extends XMutationListener
{
	private final static Logger logger=LoggerFactory.getLogger(TextBuilder.class);

	private XmlVText listenee;
	
    public TextBuilder(XmlVText listenee)
    {
    	super(new GenerateABox(),
    			PropagationType.ON_TARGET, listenee, EnumSet.allOf(MutationType.class));
    	
       	this.listenee=listenee;
    }

	@Override
	public void added(XmlMutationEvent event)
    {
		listenee.self_pollenate();
    }

	@Override
	public void removed(XmlMutationEvent event)
    {
		listenee.self_depollenate();
    }

	@Override
	public void connected(XmlMutationEvent event)
    {
//		listenee.self_pollenate();
    }

    @Override
	public void disconnected(XmlMutationEvent event)
    {
 //   	listenee.self_depollenate();
    }
}

