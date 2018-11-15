package com.github.s262316.forx.box.relayouter;

import com.github.s262316.forx.box.AtomicInline;
import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.FloatBox;
import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.relayouter.util.AfterOrEqualsLayable;
import com.github.s262316.forx.box.relayouter.util.BeforeOrEqualsLayable;
import com.github.s262316.forx.box.relayouter.util.IsFloatBox;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class Layouters
{
	public static Relayouter moreWidth(Layable subject, Layable cause, int delta)
	{
		return new MoreWidthLayouter(subject, cause, delta);
	}

	public static Relayouter moreWidthDontUnderstandWhy(Layable subject, Layable cause, int delta)
	{
		return new MoreWidthLayouter(subject, cause, delta);
	}
	
	/**
	 * the margin in the subject box needs to be increased
	 * 
	 * @param subject
	 * @param topmostAffected
	 * @param amount
	 * @return
	 */
	public static Relayouter increaseCollapsedTopMargin(Layable subject, Layable cause, int amount)
	{
		return new MoreHeightLayouter(subject, cause, amount);
	}

	public static Relayouter moreHeightDontUnderstandWhy(Layable subject, Layable cause, int amount)
	{
		return new MoreHeightLayouter(subject, cause, amount);
	}
	
	public static Relayouter moreHeight(Layable subject, Layable cause, int amount)
	{
		return new MoreHeightLayouter(subject, cause, amount);
	}
	
	public static Relayouter loadingLayouter(Layable newBox)
	{
		return new SingleLayouter(newBox);
	}
	
	public static Relayouter loadingTableLayouter(Layable tableRoot, Layable newBox)
	{
		return null;
	}

	public static Relayouter floatOnLine(AtomicInline firstAtomic, FloatBox fb)
	{
		// exclude all floats after firstAtomic and up to fb
		
		Predicate<Layable> include=Predicates.and(
			new AfterOrEqualsLayable(firstAtomic),
			Predicates.not(
				Predicates.and(
					new BeforeOrEqualsLayable(fb),
					new IsFloatBox()
				)
			)
		);
				
		return new PredicatedLayouter(BoxTypes.toRootBox(fb.root()), include);
	}

	public static Relayouter changeTopMarginSize(BlockBox container, BlockBox cause, int changeMarginTo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static Relayouter changeBottomMarginSize(Box prev, BlockBox cause, int changeMarginTo)
	{
		// TODO Auto-generated method stub
		return null;
	}
}

