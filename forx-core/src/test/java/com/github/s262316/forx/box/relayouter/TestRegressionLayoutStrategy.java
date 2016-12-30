package com.github.s262316.forx.box.relayouter;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.base.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TestRegressionLayoutStrategy
{
	@Mock
	Relayouter r1, r2;
	
	@Test
	public void testNoRegression()
	{
		RegressionLayoutStrategy strategy=new RegressionLayoutStrategy();
		ReflectionTestUtils.setField(strategy, "initial", r1);
		
		when(r1.layout(any(LayoutContext.class))).thenReturn(new LayoutResult(true, Optional.<Relayouter>absent()));

		strategy.layout();

		verify(r1).unlayout(any(LayoutContext.class));
		verify(r1).layout(any(LayoutContext.class));
	}

	@Test
	public void testRegress()
	{
		RegressionLayoutStrategy strategy=new RegressionLayoutStrategy();
		ReflectionTestUtils.setField(strategy, "initial", r1);
		
		when(r1.layout(any(LayoutContext.class))).thenReturn(new LayoutResult(false, Optional.of(r2)));
		when(r2.layout(any(LayoutContext.class))).thenReturn(new LayoutResult(true, Optional.<Relayouter>absent()));

		strategy.layout();

		verify(r1).unlayout(any(LayoutContext.class));
		verify(r1).layout(any(LayoutContext.class));
		
		verify(r2).unlayout(any(LayoutContext.class));
		verify(r2).layout(any(LayoutContext.class));
	}
}
