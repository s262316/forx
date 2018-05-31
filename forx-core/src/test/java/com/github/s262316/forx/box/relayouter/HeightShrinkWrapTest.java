package com.github.s262316.forx.box.relayouter;

import com.github.s262316.forx.box.AbsoluteBox;
import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.FloatBox;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.RootBox;
import com.github.s262316.forx.box.Text;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class HeightShrinkWrapTest
{
	@Mock
	BlockBox layable;

	@Test
	public void t1()
	{
		Map<Class<? extends Layable>, Class<? extends LayableContainer>> map=new ImmutableMap.Builder<Class<? extends Layable>,  Class<? extends LayableContainer>>()
				.put(layable.getClass(), DefaultLayableContainer.class)
				.build();
		ReflectionTestUtils.setField(HeightShrinkWrap.class, "CONTAINER_MAPPING", map);

		HeightShrinkWrap hsw=new HeightShrinkWrap();

		hsw.postLaid(layable);


	}
}
