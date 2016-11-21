package com.github.s262316.forx.css;

import java.util.Collections;
import java.util.List;

import com.github.s262316.forx.gui.CounterAction;
import com.github.s262316.forx.tree.style.DummyValue;
import com.github.s262316.forx.tree.style.MediaType;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.style.util.FilterNoneValues;
import com.github.s262316.forx.tree.style.util.ValuesHelper;
import com.github.s262316.forx.tree.visual.VElement;

import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;

public class Counters
{
	public static final DummyValue DUMMY_VALUE=new DummyValue();
	
	public static List<CounterAction> counterActions(Value value, int missingAmount)
	{
		ValueList counterValues=ValuesHelper.asValueList(value);
		Optional<String> counterName;
		Optional<Integer> counterAmount;
		Value v;
		List<CounterAction> counterActions=Lists.newArrayList();

		try
		{
			PeekingIterator<Value> it=Iterators.peekingIterator(Iterators.filter(counterValues.members.iterator(), new FilterNoneValues()));
			while(it.hasNext())
			{
				v=it.next();
				counterName=ValuesHelper.getIdentifier(v);
				
				if(it.hasNext())
				{
					v=it.peek();
					counterAmount=ValuesHelper.getInt(v);
					
					if(counterAmount.isPresent())
						it.next();
				}
				else
					counterAmount=Optional.of(missingAmount);
				
				
				counterActions.add(new CounterAction(counterName.get(), counterAmount.or(missingAmount)));
			}
			
			return counterActions;
		}
		catch(IllegalStateException ise)
		{
			// there is a counter-name missing. reject whole property
			return Collections.<CounterAction>emptyList();
		}
	}
	
    public static void handleCounters(VElement visual_element)
    {
		Value v;

		// look for any counters
		v=visual_element.getPropertyValue("counter-reset", MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO);
		List<CounterAction> resets=counterActions(v, 0);
		for(CounterAction action : resets)
		{
			visual_element.reset_counter(action.getName(), action.getAmount());
		}
		
        v=visual_element.getPropertyValue("counter-increment", MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO);
		List<CounterAction> increments=counterActions(v, 1);
		for(CounterAction action : increments)
		{
			VElement whereve=visual_element.find_counter(action.getName());
			if(whereve==null)
				whereve=visual_element;

			whereve.inc_counter(action.getName(), action.getAmount());
		}        
    }
}

