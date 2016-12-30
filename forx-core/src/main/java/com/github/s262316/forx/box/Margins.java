package com.github.s262316.forx.box;

import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.util.Overflow;

public class Margins
{
	public static boolean doesTopMarginCollapseWithParent(Layable subject)
	{
		if(BoxTypes.isBlockBox(subject))
			return doesTopMarginCollapseWithParent((BlockBox)subject);
		else
			return false;
	}
	
	public static boolean doesTopMarginCollapseWithParent(BlockBox subject)
	{
		BlockBox parent=subject.getContainer();
		
		if(parent==null)
			return false;
		if(parent.getMembersFlowing().indexOf(subject)!=0)
			return false;
		if(BoxTypes.isRootBox(parent))
			return false;
		if(subject.get_flow_context()!=parent.get_flow_context())
			return false;
		if(subject.paddingTop()>0 && subject.borderTop().width>0)
			return false;
		if(parent.paddingTop()>0 && parent.borderTop().width>0)
			return false;
		if(subject.getOverflow()==Overflow.OF_VISIBLE && parent.getOverflow()==Overflow.OF_VISIBLE)
			return false;
		
		return true;
	}
	
	public static boolean doesTopMarginCollapseWithPrevious(BlockBox subject)
	{
		BlockBox parent;
		Layable prev;
		int pos;

		parent=subject.getContainer();
		if(parent==null)
			return false;
	
		pos=parent.getMembersFlowing().indexOf(subject);
		if(pos==0) // at beginning
			return false;
		
		prev=parent.getMembersFlowing().get(pos-1);
		
		if(BoxTypes.isBlockBox(prev))
			return false;
		
		BlockBox previous=(BlockBox)prev;
		
		if(previous.getMembersFlowing().indexOf(subject)!=0)
			return false;
		if(subject.get_flow_context()!=previous.get_flow_context())
			return false;
		if(subject.paddingTop()>0 && subject.borderTop().width>0)
			return false;
		if(previous.paddingBottom()>0 && previous.borderBottom().width>0)
			return false;
		if(subject.getOverflow()==Overflow.OF_VISIBLE && previous.getOverflow()==Overflow.OF_VISIBLE)
			return false;
		
		return true;
	}

	public static boolean doesBottomMarginCollapseWithParent(Layable subject)
	{
		if(BoxTypes.isBlockBox(subject))
			return doesBottomMarginCollapseWithParent((BlockBox)subject);
		else
			return false;	
	}
	
	public static boolean doesBottomMarginCollapseWithParent(BlockBox subject)
	{
		BlockBox parent=subject.getContainer();
		
		if(parent.getMembersFlowing().indexOf(subject)!=0)
			return false;
		if(BoxTypes.isRootBox(parent))
			return false;
		if(subject.get_flow_context()!=parent.get_flow_context())
			return false;
		if(subject.paddingBottom()>0 && subject.borderBottom().width>0)
			return false;
		if(parent.paddingBottom()>0 && parent.borderBottom().width>0)
			return false;
		if(subject.getOverflow()==Overflow.OF_VISIBLE && parent.getOverflow()==Overflow.OF_VISIBLE)
			return false;
		
		return true;
	}

	public static int marginHowBig(int mt, int mb)
	{
		int margin = 0;
		/*
		 * In the case of negative margins, the maximum of the absolute values
		 * of the negative adjoining margins is deducted from the maximum of the
		 * positive adjoining margins. If there are no positive margins, the
		 * absolute maximum of the negative adjoining margins is deducted from
		 * zero
		 */
		if (mb >= 0 && mt >= 0)
			margin = Math.max(mb, mt);
		else if (mb < 0 && mt < 0)
			margin = 0 - (Math.abs(mb) - Math.abs(mt));// plus?minus?max?
		else if (mb < 0)
			margin = mt - Math.abs(mb);
		else if (mt < 0)
			margin = mb - Math.abs(mt);
		else if (mt == mb)
			margin = mt;
		else
			throw new BoxError(BoxExceptionType.BET_UNKNOWN);
	
		return margin;
	}
}



