package com.github.s262316.forx.tree.visual;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.s262316.forx.gui.CounterAction;
import com.github.s262316.forx.style.DummyValue;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.ValueList;

import com.github.s262316.forx.style.selectors.util.FilterNoneValues;
import com.github.s262316.forx.style.selectors.util.ValuesHelper;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;

public class Counters
{
	public static final DummyValue DUMMY_VALUE=new DummyValue();
	
	public static List<CounterAction> counterActions(Value value, int missingAmount)
	{
		ValueList counterValues= ValuesHelper.asValueList(value);
		Optional<String> counterName;
		Optional<Integer> counterAmount;
		Value v;
		List<CounterAction> counterActions=Lists.newArrayList();

		PeekingIterator<Value> it=Iterators.peekingIterator(Iterators.filter(counterValues.members.iterator(), new FilterNoneValues()));
		while(it.hasNext())
		{
			v=it.next();
			counterName=ValuesHelper.getIdentifier(v);
			if(!counterName.isPresent())
			{
				// there is a counter-name missing. reject whole property
				return Collections.<CounterAction>emptyList();
			}
			else
			{
				if (it.hasNext())
				{
					v = it.peek();
					counterAmount = ValuesHelper.getInt(v);

					if (counterAmount.isPresent())
						it.next();
				}
				else
					counterAmount = Optional.of(missingAmount);
			}

			counterActions.add(new CounterAction(counterName.get(), counterAmount.orElse(missingAmount)));
		}

		return counterActions;
	}
	
    public static void handleCounters(VElement visual_element)
    {
		Value v;

		// look for any counters
		v=visual_element.getPropertyValue("counter-reset", MediaType.MT_SCREEN);
		List<CounterAction> resets=counterActions(v, 0);
		for(CounterAction action : resets)
		{
			visual_element.reset_counter(action.getName(), action.getAmount());
		}
		
        v=visual_element.getPropertyValue("counter-increment", MediaType.MT_SCREEN);
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

