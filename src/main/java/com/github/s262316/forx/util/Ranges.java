package com.github.s262316.forx.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class Ranges
{
	/**
	 * returns null if all ranges are connected. otherwise returns
	 * the first range that is not connected.
	 * 
	 * @param range
	 * @return
	 */
    public static Range<Integer> firstNotConnected(RangeMap<Integer, ?> range)
    {
    	Range<Integer> first, second;
    	Iterator<Range<Integer>> firstToCompare = range.asMapOfRanges().keySet().iterator();
    	Iterator<Range<Integer>> secondToCompare = range.asMapOfRanges().keySet().iterator();

    	int advance=Iterators.advance(secondToCompare, 1);
    	if(advance!=1)
    		return null;
    		
    	while(secondToCompare.hasNext())
    	{
    		first=firstToCompare.next();
    		second=secondToCompare.next();
    		
    		if(!first.isConnected(second))
    			return second;
    	}
    	
    	return null;
    }
    
    /**
     * intersection of all ranges. or empty range if there is no intersection
     * 
     * @param ranges
     * @return
     */
    public static Range<Integer> intersection(Iterable<Range<Integer>> ranges)
    {
    	Range<Integer> runningTotalIntersection=Range.all();
    	
    	for(Range<Integer> r : ranges)
    	{
    		if(!runningTotalIntersection.isConnected(r))
    			return Ranges.emptyRange();
    		
    		runningTotalIntersection=runningTotalIntersection.intersection(r);
    	}
    	
    	return runningTotalIntersection;
    }
    
    public static Range<Integer> emptyRange()
    {
    	return Range.closedOpen(0, 0);
    }
    
    public static int size(Range<Integer> range)
    {
    	Preconditions.checkArgument(range.upperBoundType()==BoundType.OPEN, "Upper bound must be open");
    	Preconditions.checkArgument(range.lowerBoundType()==BoundType.CLOSED, "Lower bound must be closed");
    	
    	return range.upperEndpoint()-range.lowerEndpoint();
    }
    
    public static Range<Integer> getFirst(RangeMap<Integer, ?> ranges)
    {
    	return Iterables.get(ranges.asMapOfRanges().keySet(), 0);
    }
    
    public static <V> RangeMap<Integer, V> copyOf(RangeMap<Integer, V> range)
    {
    	TreeRangeMap<Integer, V> copy=TreeRangeMap.create();
    	
    	copy.putAll(range);
    	
    	return copy;
    }
    
    /**
     * other must be within at least one
     * 
     * @param ranges
     * @param other
     * @return
     */
    public static <V> boolean encloses(RangeMap<Integer, V> ranges, Range<Integer> other)
    {
    	ImmutableRangeSet.Builder<Integer> builder=ImmutableRangeSet.builder();
    	
    	for(Range<Integer> r : ranges.asMapOfRanges().keySet())
    		builder.add(r);
    	
    	ImmutableRangeSet<Integer> rangeSet=builder.build();
    	
    	return rangeSet.encloses(other);
    }
    
    public static List<Range<Integer>> listVoids(Collection<Range<Integer>> rs)
    {
    	return Collections.<Range<Integer>>emptyList();    	
    }
}


