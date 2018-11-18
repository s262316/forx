package com.github.s262316.forx.box;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.cast.BoxTypes;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;


public class Flowspace
{
 	private final static Logger logger=LoggerFactory.getLogger(Flowspace.class);
	private List<Line> lines=new LinkedList<Line>();
    private Map<Line, LinkedList<AtomicInline>> atomic_locations=new HashMap<Line, LinkedList<AtomicInline>>();
    private Map<InlineBox, LinkedList<Line>> inlinebox_lines=new HashMap<InlineBox, LinkedList<Line>>();

    public int inlineboxesOnLineCount(Line line)
    {
    	Set<Integer> boxIds=new HashSet<>();
    	
    	if(atomic_locations.containsKey(line))
    	{
    		LinkedList<AtomicInline> atomicsOnLine=atomic_locations.get(line);
    		for(AtomicInline atomic : atomicsOnLine)
    		{
    			boxIds.add(atomic.container().getId());
    		}
    	}
    	
    	return boxIds.size();
    }
    
    public AtomicInline back_atomic(Line line)
    {
        LinkedList<AtomicInline> atomics=atomic_locations.get(line);
        AtomicInline ainl=atomics.getLast();
        return ainl;
    }

    public AtomicInline front_atomic(Line line)
    {
        LinkedList<AtomicInline> atomics=atomic_locations.get(line);
        AtomicInline ainl=atomics.getFirst();
        return ainl;
    }
    // this will keep the lines in order

    public void add_line(Line line)
    {
        boolean found=false;
        Line l2;
        int index=0;
        while(!found && index < lines.size())
        {
            l2=lines.get(index);
            if(line.top() > l2.top())
                found=true;
            else
            	index++;
        }
        lines.add(index, line);
    }

    // this will keep the atomics in order!
    public void assoc_atomic(Line line, AtomicInline ail)
    {
        LinkedList<AtomicInline> aill=atomic_locations.get(line);
        AtomicInline ail2;
        boolean found=false;
 //       int index=0;

		logger.debug("assoc_atomic");

		if(!atomic_locations.containsKey(line))
		{
			aill=new LinkedList<AtomicInline>();
			atomic_locations.put(line, aill);
		}

		aill=atomic_locations.get(line);

//        while(!found && index < aill.size())
//        {
//            ail2=aill.get(index);
//
//            if(ail.top() > ail2.top())
//                found=true;
//        }

        aill.add(ail);
        assoc_inlinebox(line, BoxTypes.toInlineBox(ail.container()));
        // dump();

		logger.debug("assoc_atomic exiting");
    }

    public void disassoc_atomic(Line line, AtomicInline ail)
    {
        if(atomic_locations.containsKey(line))
        {
            List<AtomicInline> ail1=atomic_locations.get(line);
            ail1.remove(ail);
        }
        // dump();
    }

    public void disassoc_inlinebox(Line line, InlineBox ilb)
    {
        // we may get calls that try to disassociate a line
        // that has already been disassociated - this will be
        // after doing a relayout in the middle of a group of atomics

        if(inlinebox_lines.containsKey(ilb))
        {
            List<Line> linelist=inlinebox_lines.get(ilb);
            linelist.remove(line);
            if(atomic_count(line) == 0)
            {

                atomic_locations.remove(line);
                lines.remove(line);
            }
        }
        // dump();
    }

    public void assoc_inlinebox(Line line, InlineBox ilb)
    {
        LinkedList<Line> linelist;
        Line l2;
        boolean found=false;
//        int index=0;

		logger.debug("assoc_inlinebox");

		if(!inlinebox_lines.containsKey(ilb))
		{
			linelist=new LinkedList<Line>();
			inlinebox_lines.put(ilb, linelist);
		}

		linelist=inlinebox_lines.get(ilb);


//        while(!found && index < linelist.size())
//        {
//            l2=linelist.get(index);
//
//            if(line.top() > l2.top())
//                found=true;
//            else
//            	index++;
//        }

        if(!linelist.contains(line))
        {
            linelist.add(line);
            if(BoxTypes.isInline(ilb.container()) == true)
                assoc_inlinebox(line, BoxTypes.toInlineBox(ilb.container()));
        }

		logger.debug("assoc_inlinebox exiting");
    }

    public List<Line> line_list(InlineBox ilb)
    {
		if(inlinebox_lines.containsKey(ilb))
	        return ImmutableList.copyOf(inlinebox_lines.get(ilb));

	    return Collections.emptyList();
    }

    public Line front_line(InlineBox ilb)
    {
        return inlinebox_lines.get(ilb).getFirst();
    }

    public Line back_line(InlineBox ilb)
    {
        return inlinebox_lines.get(ilb).getLast();
    }

    public int line_count(InlineBox ilb)
    {
		if(inlinebox_lines.containsKey(ilb))
	        return inlinebox_lines.get(ilb).size();

	    return 0;
    }

    public int atomic_count(Line line)
    {
		if(atomic_locations.containsKey(line))
	        return atomic_locations.get(line).size();

	    return 0;
    }
    
    public List<AtomicInline> atomicsOnLine(Line line)
    {
        return MoreObjects.firstNonNull(
        		ImmutableList.copyOf(atomic_locations.get(line)),
        		Collections.<AtomicInline>emptyList());
    }    

    public boolean is_on_line(InlineBox ilb, Line line)
    {
        List<Line> ilb_lines=inlinebox_lines.get(ilb);
        return ilb_lines.contains(line);
    }

    @Override
    public String toString()
    {
        String str="";
        for(Line line : lines)
        {
            str+="-line : " + line + "\n";
        }
        for(Map.Entry<Line, LinkedList<AtomicInline>> alit : atomic_locations.entrySet())
        {
            str+="line : " + alit.getKey() + "\n";
            LinkedList<AtomicInline> linelist=alit.getValue();
            for(AtomicInline llit : linelist)
            {
                str+=" atomic " + llit.id + "\n";
            }
        }
        for(Map.Entry<InlineBox, LinkedList<Line>> ilit : inlinebox_lines.entrySet())
        {
            str+="inlinebox : " + ilit.getKey().id + "\n";
            List<Line> linelist=ilit.getValue();
            for(Line lit : linelist)
            {
                str+=" line " + lit + "\n";
            }
        }
        return str;
    }
}
