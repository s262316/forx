package com.github.s262316.forx.box.relayouter.support;

import java.util.List;

import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.relayouter.AbstractRelayouter;
import com.github.s262316.forx.box.relayouter.FullLayouter;

public class AllLayouterForTest extends AbstractRelayouter
{
	private List<Layable> testData;
	
	public AllLayouterForTest(List<Layable> testData)
	{
		this.testData=testData;
	}
	
	@Override
	public List<Layable> getAffected()
	{
		return testData;
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof AllLayouterForTest))
			return false;

		AllLayouterForTest rhs=(AllLayouterForTest)other;
		
		return testData.equals(rhs.testData);		
	}

}
