package com.github.s262316.forx.box;

import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.util.Overflow;
import com.github.s262316.forx.tree.events2.XmlMouseEvent;

import com.google.common.base.Objects;

public class CollapsedMargins
{
	private boolean topMarginCollapsesIn=false;
	private boolean topMarginCollapsesOut=false;
	private boolean bottomMarginCollapsesIn=false;
	private boolean bottomMarginCollapsesOut=false;

	public CollapsedMargins()
	{}
	
	public CollapsedMargins(boolean topMarginCollapsesIn, boolean topMarginCollapsesOut, boolean bottomMarginCollapsesIn, boolean bottomMarginCollapsesOut)
	{
		this.topMarginCollapsesIn = topMarginCollapsesIn;
		this.topMarginCollapsesOut = topMarginCollapsesOut;
		this.bottomMarginCollapsesIn = bottomMarginCollapsesIn;
		this.bottomMarginCollapsesOut = bottomMarginCollapsesOut;
	}

	public boolean isTopMarginCollapsesIn()
	{
		return topMarginCollapsesIn;
	}

	public void setTopMarginCollapsesIn(boolean topMarginCollapsesIn)
	{
		this.topMarginCollapsesIn = topMarginCollapsesIn;
	}

	public boolean isTopMarginCollapsesOut()
	{
		return topMarginCollapsesOut;
	}

	public void setTopMarginCollapsesOut(boolean topMarginCollapsesOut)
	{
		this.topMarginCollapsesOut = topMarginCollapsesOut;
	}

	public boolean isBottomMarginCollapsesIn()
	{
		return bottomMarginCollapsesIn;
	}

	public void setBottomMarginCollapsesIn(boolean bottomMarginCollapsesIn)
	{
		this.bottomMarginCollapsesIn = bottomMarginCollapsesIn;
	}

	public boolean isBottomMarginCollapsesOut()
	{
		return bottomMarginCollapsesOut;
	}

	public void setBottomMarginCollapsesOut(boolean bottomMarginCollapsesOut)
	{
		this.bottomMarginCollapsesOut = bottomMarginCollapsesOut;
	}

	public static CollapsedMargins calculateCollapsedMargins(BlockBox boxMember, boolean clearanceUsed)
	{
		CollapsedMargins cm=new CollapsedMargins();
		
		if (clearanceUsed == false && BoxTypes.isTableBox(boxMember) == false)
		{
			if (boxMember.getOverflow() == Overflow.OF_VISIBLE)
			{
				cm.setTopMarginCollapsesOut(true);
				cm.setBottomMarginCollapsesOut(true);

				if (boxMember.borderTop().width == 0 && boxMember.paddingTop() == 0)
					cm.setTopMarginCollapsesIn(true);
					
				if (boxMember.borderBottom().width == 0 && boxMember.paddingBottom() == 0)
					cm.setBottomMarginCollapsesIn(true);
			}
		}
		
		return cm;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(topMarginCollapsesIn, topMarginCollapsesOut, bottomMarginCollapsesIn, bottomMarginCollapsesOut);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {return false; }
		
		CollapsedMargins rhs = (CollapsedMargins) obj;
		
		return Objects.equal(topMarginCollapsesIn, rhs.topMarginCollapsesIn) &&
				Objects.equal(topMarginCollapsesOut, rhs.topMarginCollapsesOut) &&
				Objects.equal(bottomMarginCollapsesIn, rhs.bottomMarginCollapsesIn) &&
				Objects.equal(bottomMarginCollapsesOut, rhs.bottomMarginCollapsesOut);
		
	}

	@Override
	public String toString()
	{
		return Objects.toStringHelper(this)
				.add("topMarginCollapsesIn", topMarginCollapsesIn)
				.add("topMarginCollapsesOut", topMarginCollapsesOut)
				.add("bottomMarginCollapsesIn", bottomMarginCollapsesIn)
				.add("bottomMarginCollapsesOut", bottomMarginCollapsesOut)
				.toString();
	}
}


