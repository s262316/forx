package com.github.s262316.forx.box;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.util.FloatPosition;
import com.github.s262316.forx.util.Ranges;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

class FlowContext
{
	private final static Logger logger=LoggerFactory.getLogger(FlowContext.class);
    private int _left, _right, _top;

    public FlowContext()
    {
    }

    /**
     * key = y range, value = x range
     */
    private RangeMap<Integer, Range<Integer>> flowAreas=TreeRangeMap.create();
    
	public void reset(int x1, int x2, int y)
    {
    	flowAreas.clear();    	
    	flowAreas.put(Range.atLeast(y), Range.closedOpen(x1, x2+1));

        _left=x1;
        _right=x2;
        _top=y;
    }    

	public Rectangle metricsNoClear(final int atLeastX, final int atMostX, final int atLeastY, final int atLeastWidth, final int height)
    {
    	Range<Integer> vertSpaceNeeded;
    	Range<Integer> allowedHorizRange;
    	Range<Integer> availHorizSpace;
    	Range<Integer> first;
    	RangeMap<Integer, Range<Integer>> rangeToTry;
    	boolean horizDidNotFit=false;
    	
    	logger.debug("atLeastX={},atMostX={},atLeastY={},atLeastWidth={},height={}", atLeastX, atMostX, atLeastY, atLeastWidth, height);

    	Preconditions.checkState(_right>0, "reset() must be called before using FlowContext");
    	Preconditions.checkArgument(atMostX>=atLeastX, "atLeastX (%s) value is greater than atMostX value (%s)", atLeastX, atMostX);
    	Preconditions.checkArgument(atLeastWidth>0, "atLeastWidth value (%s) is 0 or less", atLeastWidth);
    	Preconditions.checkArgument(height>0, "height value (%s) is 0 or less", height);
		Preconditions.checkArgument(atMostX-atLeastX+1 >= atLeastWidth, "requested width (%s) does not fit into least/most (%s-%s) constraints", atLeastWidth, atLeastX, atMostX);

    	allowedHorizRange=Range.closedOpen(atLeastX, atMostX+1);

    	// make a copy of the working rangemap
    	rangeToTry=Ranges.copyOf(flowAreas.subRangeMap(Range.atLeast(atLeastY)));
    	// top of range, not of atLeastY
    	first=Ranges.getFirst(rangeToTry);
    	vertSpaceNeeded=Range.closedOpen(first.lowerEndpoint(), first.lowerEndpoint()+height);

		logger.debug("vertRangeNeeded rangeToTry={},vertSpaceNeeded={}", rangeToTry.toString(), vertSpaceNeeded.toString());

		do
		{
			while(!Ranges.encloses(rangeToTry, vertSpaceNeeded) || horizDidNotFit==true)
	    	{
	    		// keep trying til for a vert fit
	    		// if asked space gone below avail, slice off first
	    		rangeToTry.remove(first);
	    		first=Ranges.getFirst(rangeToTry);
	        	vertSpaceNeeded=Range.closedOpen(first.lowerEndpoint(), first.lowerEndpoint()+height);
	
	        	horizDidNotFit=false;
	        	
	    		logger.debug("re-vertRangeNeeded rangeToTry={},vertSpaceNeeded={}", rangeToTry.toString(), vertSpaceNeeded.toString());
	    	}
	
	    	// do horiz fit
	    	Collection<Range<Integer>> horizontalRanges=rangeToTry.subRangeMap(Range.lessThan(first.lowerEndpoint()+height)).asMapOfRanges().values();
	    	availHorizSpace=Ranges.intersection(horizontalRanges);
	    	
			logger.debug("horizRangeNeeded availHorizSpace={},allowedHorizRange={},atLeastWidth={}", availHorizSpace.toString(), allowedHorizRange.toString(), atLeastWidth);	    	
			
			horizDidNotFit=true; // necessary for the innher while loop to peel off the first range when horiz fails

		}while(allowedHorizRange.isConnected(availHorizSpace)==false ||
				Ranges.size(allowedHorizRange.intersection(availHorizSpace)) < atLeastWidth);

		// the answer
		return new Rectangle(
				Math.max(atLeastX, availHorizSpace.lowerEndpoint()),
				Math.max(rangeToTry.span().lowerEndpoint(), atLeastY),
				Ranges.size(allowedHorizRange.intersection(availHorizSpace)),
				height);
    }

	public int highest_flow_left_clear(int atLeastY)
    {
    	RangeMap<Integer, Range<Integer>> rangeToTry;
    	
    	Preconditions.checkState(_right>0, "reset() must be called before using FlowContext");
    	
    	rangeToTry=flowAreas.subRangeMap(Range.atLeast(atLeastY));
    	
    	for(Map.Entry<Range<Integer>, Range<Integer>> e : rangeToTry.asMapOfRanges().entrySet())
    	{
    		if(e.getValue().contains(_left))
    			return e.getKey().lowerEndpoint();
    	}

    	throw new IllegalStateException("No max-width infinite flow area found");
    }

	public int highest_flow_right_clear(int atLeastY)
    {
    	RangeMap<Integer, Range<Integer>> rangeToTry;
    	
    	Preconditions.checkState(_right>0, "reset() must be called before using FlowContext");
    	
    	rangeToTry=flowAreas.subRangeMap(Range.atLeast(atLeastY));
    	
    	for(Map.Entry<Range<Integer>, Range<Integer>> e : rangeToTry.asMapOfRanges().entrySet())
    	{
    		if(e.getValue().contains(_right))
    			return e.getKey().lowerEndpoint();
    	}

    	throw new IllegalStateException("No max-width infinite flow area found");
    }

	public int highest_flow_both_clear(int atLeastY)
    {
    	RangeMap<Integer, Range<Integer>> rangeToTry;
    	
    	Preconditions.checkState(_right>0, "reset() must be called before using FlowContext");
    	
    	rangeToTry=flowAreas.subRangeMap(Range.atLeast(atLeastY));
    	
    	for(Map.Entry<Range<Integer>, Range<Integer>> e : rangeToTry.asMapOfRanges().entrySet())
    	{
    		if(e.getValue().contains(_left) && e.getValue().contains(_right))
    			return e.getKey().lowerEndpoint();
    	}

    	throw new IllegalStateException("No max-width infinite flow area found");
    }
	
	public void takeout_flow_area(int y1, int y2, int x, FloatPosition fp)
    {
    	List<Range<Integer>> vertsToDelete=Lists.newArrayList();
    	Map<Range<Integer>, Range<Integer>> vertsToChange=new HashMap<>();
    	Range<Integer> vertTakeout;
    	Range<Integer> horizTakeout;

    	Preconditions.checkState(_right>0, "reset() must be called before using FlowContext");
    	Preconditions.checkArgument(fp!=FloatPosition.NONE);
    	Preconditions.checkArgument(y2>=y1, "y1 value(%s) is greater than y2 value (%s)", y1, y2);
    	
    	vertTakeout=Range.closedOpen(y1, y2+1);
    	if(fp==FloatPosition.LEFT)
    		horizTakeout=Range.closedOpen(_left, x+1);
    	else // fp_right
    		horizTakeout=Range.closedOpen(x, _right+1);

    	for(Map.Entry<Range<Integer>, Range<Integer>> e : flowAreas.asMapOfRanges().entrySet())
    	{
    		if(e.getKey().isConnected(vertTakeout) && !e.getKey().intersection(vertTakeout).isEmpty())
    		{
	    		Range<Integer> vertIntersection=e.getKey().intersection(vertTakeout);
	    		Range<Integer> horizIntersection=e.getValue().intersection(horizTakeout);
	    		
	    		if(e.getKey().equals(vertTakeout) && e.getValue().equals(horizTakeout))
	    		{
		            // floatable is exactly the same size as the flow area
		            // +---+
		            // |///|
		            // |///|
		            // +---+
	    			vertsToDelete.add(e.getKey());
	    		}
	            else if(y1==e.getKey().lowerEndpoint() && e.getValue().equals(horizTakeout))
	            {
	                // complete top section of the flow area is to be removed
	                // +-------+
	                // |///////|
	                // |///////|
	                // |       |
	                // |       |
	                // +-------+
	            	vertsToDelete.add(e.getKey());
	            	vertsToChange.put(Range.closedOpen(vertIntersection.upperEndpoint(), e.getKey().upperEndpoint()), e.getValue());
	            }
	            else if(e.getKey().hasUpperBound() && y2+1==e.getKey().upperEndpoint() && e.getValue().equals(horizTakeout))
	            {
	                // complete bottom section of the flow area is to removed
	                // +-------+
	                // |       |
	                // |       |
	                // |///////|
	                // |///////|
	                // +-------+
	            	vertsToDelete.add(e.getKey());
	            	vertsToChange.put(Range.closedOpen(e.getKey().lowerEndpoint(), vertIntersection.lowerEndpoint()), e.getValue());
	            }
	            else if(e.getKey().equals(vertTakeout) && e.getValue().lowerEndpoint().equals(horizIntersection.lowerEndpoint()))
	            {
	                // +-------+
	                // |///    |
	                // |///    |
	                // |///    |
	                // |///    |
	                // +-------+
	            	vertsToChange.put(e.getKey(), Range.closedOpen(horizIntersection.upperEndpoint(), e.getValue().upperEndpoint()));
	            }
	            else if(e.getKey().equals(vertTakeout) && e.getValue().upperEndpoint().equals(horizIntersection.upperEndpoint()))
	            {
	                // +-------+
	                // |    ///|
	                // |    ///|
	                // |    ///|
	                // |    ///|
	                // +-------+
	            	vertsToChange.put(e.getKey(), Range.closedOpen(e.getValue().lowerEndpoint(), horizIntersection.lowerEndpoint()));
	            }
	            else if(vertIntersection.lowerEndpoint().equals(e.getKey().lowerEndpoint()) && horizIntersection.lowerEndpoint().equals(e.getValue().lowerEndpoint()))
	            {
	                // +-------+
	                // |////   |
	                // |////   |
	                // |       |
	                // |       |
	                // +-------+
	            	vertsToDelete.add(e.getKey());
	            	vertsToChange.put(vertIntersection, Range.closedOpen(horizIntersection.upperEndpoint(), e.getValue().upperEndpoint()));
	            	if(e.getKey().hasUpperBound())
	            		vertsToChange.put(Range.closedOpen(vertIntersection.upperEndpoint(), e.getKey().upperEndpoint()), e.getValue());
	            	else
	            		vertsToChange.put(Range.atLeast(vertIntersection.upperEndpoint()), e.getValue());
	            }
	            else if(vertIntersection.lowerEndpoint().equals(e.getKey().lowerEndpoint()) && horizIntersection.upperEndpoint().equals(e.getValue().upperEndpoint() ))
	            {
	                // +-------+
	                // |   ////|
	                // |   ////|
	                // |       |
	                // |       |
	                // +-------+            	
	            	vertsToDelete.add(e.getKey());
	            	vertsToChange.put(vertIntersection, Range.closedOpen(e.getValue().lowerEndpoint(), horizIntersection.lowerEndpoint()));
	            	if(e.getKey().hasUpperBound())
	            		vertsToChange.put(Range.closedOpen(vertIntersection.upperEndpoint(), e.getKey().upperEndpoint()), e.getValue());
	            	else
	            		vertsToChange.put(Range.atLeast(vertIntersection.upperEndpoint()), e.getValue());
	            	
	            }
	            else if(e.getKey().hasUpperBound() && vertIntersection.upperEndpoint().equals(e.getKey().upperEndpoint()) && horizIntersection.lowerEndpoint().equals(e.getValue().lowerEndpoint()))
	            {
	                // +-------+
	                // |       |
	                // |       |
	                // |////   |
	                // |////   |
	                // +-------+
	            	vertsToDelete.add(e.getKey());
	            	vertsToChange.put(Range.closedOpen(e.getKey().lowerEndpoint(), vertIntersection.lowerEndpoint()), e.getValue());
	            	vertsToChange.put(vertIntersection, Range.closedOpen(horizIntersection.upperEndpoint(), e.getValue().upperEndpoint()));
	            }
	            else if(e.getKey().hasUpperBound() && vertIntersection.upperEndpoint().equals(e.getKey().upperEndpoint()) && horizIntersection.upperEndpoint().equals(e.getValue().upperEndpoint()))
	            {
	                // +-------+
	                // |       |
	                // |       |
	                // |   ////|
	                // |   ////|
	                // +-------+
	            	vertsToDelete.add(e.getKey());
	            	vertsToChange.put(Range.closedOpen(e.getKey().lowerEndpoint(), vertIntersection.lowerEndpoint()), e.getValue());
	            	vertsToChange.put(vertIntersection, Range.closedOpen(e.getValue().lowerEndpoint(), horizIntersection.lowerEndpoint()));
	            }
	            else if(horizIntersection.lowerEndpoint().intValue()==e.getValue().lowerEndpoint().intValue())
	            {
	                // +-------+
	                // |       |
	                // |////   |
	                // |////   |
	                // |       |
	                // +-------+
	            	vertsToDelete.add(e.getKey());
	            	vertsToChange.put(Range.closedOpen(e.getKey().lowerEndpoint(), vertIntersection.lowerEndpoint()), e.getValue());
	            	vertsToChange.put(vertIntersection, Range.closedOpen(horizIntersection.upperEndpoint(), e.getValue().upperEndpoint()));
	            	if(e.getKey().hasUpperBound())
	            		vertsToChange.put(Range.closedOpen(vertIntersection.upperEndpoint(), e.getKey().upperEndpoint()), e.getValue());
	            	else
	            		vertsToChange.put(Range.atLeast(vertIntersection.upperEndpoint()), e.getValue());
	            }
	            else if(horizIntersection.upperEndpoint().intValue()==e.getValue().upperEndpoint().intValue())
	            {
	                // +-------+
	                // |       |
	                // |   ////|
	                // |   ////|
	                // |       |
	                // +-------+
	            	vertsToDelete.add(e.getKey());
	            	vertsToChange.put(Range.closedOpen(e.getKey().lowerEndpoint(), vertIntersection.lowerEndpoint()), e.getValue());
	            	vertsToChange.put(vertIntersection, Range.closedOpen(e.getValue().lowerEndpoint(), horizIntersection.lowerEndpoint()));
	            	if(e.getKey().hasUpperBound())
	            		vertsToChange.put(Range.closedOpen(vertIntersection.upperEndpoint(), e.getKey().upperEndpoint()), e.getValue());
	            	else
	            		vertsToChange.put(Range.atLeast(vertIntersection.upperEndpoint()), e.getValue());
	            }
    		}
    	}
    	
    	for(Range<Integer> deleteMe : vertsToDelete)
    		flowAreas.remove(deleteMe);
    	
    	for(Map.Entry<Range<Integer>, Range<Integer>> setMe : vertsToChange.entrySet())
    		flowAreas.put(setMe.getKey(), setMe.getValue());
    	
    	Preconditions.checkState(!flowAreas.span().hasUpperBound());
    }

	public void putback(int y1, int y2, int x1, int x2, FloatPosition fp)
	{
		Range<Integer> yPutback=Range.closedOpen(y1, y2+1);
    	Map<Range<Integer>, Range<Integer>> vertsToChange=new HashMap<>();
    	
		Set<Range<Integer>> ys=flowAreas.subRangeMap(yPutback).asMapOfRanges().keySet();
		List<Range<Integer>> voidAreas=Ranges.listVoids(ys);
		
		for(Map.Entry<Range<Integer>, Range<Integer>> e : flowAreas.subRangeMap(yPutback).asMapOfRanges().entrySet())
		{
			if(fp==FloatPosition.LEFT)
			{
				Range<Integer> newX=Range.closedOpen(x1, e.getValue().upperEndpoint());

				// newX.right must touch e.left
				Preconditions.checkArgument(x2+1==e.getValue().lowerEndpoint(), "x2=%s does not connect with left of flowarea %s", x2, e.getValue().lowerEndpoint());
				vertsToChange.put(e.getKey(), newX);
			}
			else
			{
				Range<Integer> newX=Range.closedOpen(e.getValue().lowerEndpoint(), x2+1);
				
				// newX.left must touch e.right
				Preconditions.checkArgument(x1==e.getValue().upperEndpoint(), "x1=%s does not connect with left of flowarea %s", x1, e.getValue().upperEndpoint());
				vertsToChange.put(e.getKey(), newX);
			}
		}

    	for(Map.Entry<Range<Integer>, Range<Integer>> setMe : vertsToChange.entrySet())
    		flowAreas.put(setMe.getKey(), setMe.getValue());		
		
		// put the void areas in
		for(Range<Integer> voidArea : voidAreas)
		{
			flowAreas.put(voidArea, Range.closedOpen(x1, x2));
		}
	}
}




