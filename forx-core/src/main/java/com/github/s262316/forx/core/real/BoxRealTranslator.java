package com.github.s262316.forx.core.real;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.geometry.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Inline;
import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.core.WebpageHolder;
import com.github.s262316.forx.tree.XmlNode;
import com.github.s262316.forx.tree.visual.XmlVElement;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BoxRealTranslator implements BoxRealMapping
{
	private final static Logger logger=LoggerFactory.getLogger(BoxRealTranslator.class);
	
	private DynamicAABBTree<Onscreen> realPositions;
	private Map<Onscreen, Layable> mapping1=new HashMap<>();
	private Map<Layable, XmlNode> mapping2=new HashMap<>();
	
	@Autowired
	public BoxRealTranslator(WebpageHolder webpageHolder)
	{
		logger.debug("ctor");
		
		this.realPositions=new DynamicAABBTree<>(100);
	}

	@Override
	public void update(Inline inl, int x, int y, int width, int height)
	{
		Onscreen onscreenPosition=new Onscreen(x, y, width, height);

		realPositions.add(onscreenPosition);
		mapping1.put(onscreenPosition, inl);
	}

	@Override
	public void update(Box box, int x, int y, int width, int height)
	{
		Onscreen onscreenPosition=new Onscreen(x, y, width, height);
		
		realPositions.add(onscreenPosition);
		mapping1.put(onscreenPosition, box);
	}

	@Override
	public void add(XmlVElement element, Box visualBox)
	{
		Preconditions.checkNotNull(element);
		Preconditions.checkNotNull(visualBox);
		
		mapping2.put(visualBox, element);
	}

	@Override
	public List<XmlNode> componentsAtPoint(int x, int y)
	{
		AABB a1=new AABB(x, y, x+1, y+1);
		
		List<Onscreen> collisions=realPositions.detect(a1);
		List<XmlNode> nodesHit=Lists.newArrayList();
		
		for(Onscreen onscreen : collisions)
		{
			Layable box=mapping1.get(onscreen);
			XmlNode node=mapping2.get(box);
			
			if(node!=null)
				nodesHit.add(node);
		}
		
		return nodesHit;
	}

	@Override
	public void invalidateRealPositions()
	{
		realPositions.clear();
		mapping1.clear();
	}
	
}



