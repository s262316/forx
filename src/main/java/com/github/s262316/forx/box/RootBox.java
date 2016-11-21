package com.github.s262316.forx.box;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.properties.MarginDescriptor;
import com.github.s262316.forx.box.properties.PropertyBoxAdaptor;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.relayouter.LayoutResult;
import com.github.s262316.forx.box.relayouter.Relayouter;
import com.github.s262316.forx.box.relayouter.util.LayoutUtils;
import com.github.s262316.forx.box.util.SizeResult;
import com.github.s262316.forx.util.ZIndexComparator;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

public class RootBox extends BlockBox
{
	private final static Logger logger=LoggerFactory.getLogger(RootBox.class);
	private TreeSet<AbsoluteBox> positionedBoxes=Sets.newTreeSet(new ZIndexComparator());
 	private boolean use_body_background;
 	private FlowContext flowContext=new FlowContext();

	public RootBox(Visual v, Drawer d)
	{
		super(v, d, null);
	}

//    @Override
//	public void compute_properties()
//	{
//		BorderDescriptor bd=new BorderDescriptor();
//		TextProperties tp=new TextProperties();
//		WordProperties wp=new WordProperties();
//		LineDescriptor ld=new LineDescriptor();
//		int space_width=0, space_height=0;
//		ColourDescriptor cd=new ColourDescriptor();
//		Font f;
//		BlockProperties bp=new BlockProperties();
//
//		// important to get this before any other properties
//		f=visual.workOutFontProperties(this);
//		set_font(f);
//		visual.calculateBorders(this, bd);
//		visual.workOutTextProperties(this, tp);
//		visual.workOutWordProperties(this, wp);
//		visual.workOutLineProperties(this, ld);
//		visual.workoutColours(this, cd);
//		visual.workoutBlockProperties(this, bp);
//		set_borders(bd);
//		_line_height=(int)ld.lineHeight;
//		// no vertical-align for block boxes
//		_letter_spacing=wp.letter_spacing;
////		if(wp.word_spacing==SpecialLength.SL_AUTO)
////			_word_spacing=0;
////		else
//			_word_spacing=wp.word_spacing;
//
//		_text_indent=tp.text_indent;
//		_text_align=tp.text_align;
//		_foreground_colour=cd.foreground;
//		_clearance=bp.clear;
//		_overflow=bp.overflow;
//	}

    @Override
	public SizeResult compute_dimensions()
	{
		SizeResult size=new SizeResult();
		MarginDescriptor md=new MarginDescriptor();

		visual.computeMarginProperties(new PropertyBoxAdaptor(this), md);
		Rectangle r1=visual.getGraphicsContext().get_browser_area_limits();

		size.marginBottom.set(md.bottom);
		size.marginLeft.set(md.left);
		size.marginRight.set(md.right);
		size.marginTop.set(md.top);
		size.width.set(Math.max(r1.width, getFutureWidth().or(0)));
		size.height.set(Math.max(r1.height, getFutureHeight().or(0)));

		return size;
	}

	public void selfCalculatePosition()
	{
		SizeResult size;
		
		size=compute_dimensions();
		
		set_margins(size.marginLeft.value, size.marginTop.value, size.marginRight.value, size.marginBottom.value);
		set_dimensions(size.width.value, size.height.value);
		super.setAutoHeight(true);
		
		super.set_position(0, 0);
		
		flowContext.reset(left(), right(), top());
	}
	
	public void selfUncalculatePosition()
	{
		LayoutUtils.invalidatePosition(this);
	}
	
    @Override
	public FlowContext get_flow_context()
	{
		return flowContext;
	}

    @Override
	public Box get_flow_context_box()
	{
		return this;
	}

//    @Override
//	public Layable resize_height(int from, int amount)
//	{
//		Layable requester=all.get(from);
//		// force the change in height
//		requester.change_height(amount);
//		if(requester.bottom() > contentBottom())
//		{
//			change_height(requester.bottom()-contentBottom());
//			return this;
//		}
//		else
//			return requester;
//	}
//
//    @Override
//	public Box resize_width(int from, int amount)
//	{
//		Layable requester=all.get(from);
//		// force the change in width
//		requester.change_width(amount);
//		if(requester.right() > contentRight())
//			change_width(requester.right()-contentRight());
//		return this;
//	}

    @Override
	public void set_width(int width)
	{
		super.set_width(width);
		visual.getGraphicsContext().setContentWidth(width());
		// also set width of body element
		if(all.size()>0)
			all.getFirst().set_width(width);
	}

    @Override
	public void set_height(int height)
	{
		super.set_height(height);
		visual.getGraphicsContext().setContentHeight(height());
		// also set height of body element
		if(all.size()>0)
			all.getFirst().set_height(height);
	}

    @Override
	public void change_height(int difference)
	{
		super.change_height(difference);
		visual.getGraphicsContext().setContentHeight(height());
		// also set height of body element
		if(all.size()>0)
			all.getFirst().set_height(height());
	}

    @Override
	public void change_width(int difference)
	{
		super.change_width(difference);
		visual.getGraphicsContext().setContentWidth(width());
		// also set width of body element
		if(all.size()>0)
			all.getFirst().set_height(width());
	}

//    @Override
//	public void absBackStatic(AbsoluteBox ab)
//	{	
//		all.add(ab);
//		positionedBoxes.add(ab);
//		
//		ab.set_container(this);
//		ab.setStaticContainer(this);
//		ab.computeProperties();
//		
//        LayoutUtils.doLoadingLayout(ab);
//	}
//
//    @Override
//	public void absBack(AbsoluteBox ab)
//	{	
//    	absBackStatic(ab);
//	}    

	public Iterator<AbsoluteBox> absIterator()
	{
		return positionedBoxes.iterator();
	}

	public void adopt_body_background(boolean adopt)
	{
		use_body_background=adopt;
	}

	public boolean has_adopted_body_background()
	{
		return use_body_background;
	}

	@Override
	public int canStretchHeight(int amount)
	{
		if(isAutoHeight())
			return amount;
		else
			return 0;
	}  	    
	
}

