package com.github.s262316.forx.box.adders;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Inline;

@ExtendWith(MockitoExtension.class)
public class DefaultAdderTest
{
	@Mock
	Box subject;
	@Mock
	Box boxChild;
	@Mock
	Inline inlineChild;

	@Test
	public void addBox()
	{
		DefaultAdder defaultAdder=new DefaultAdder(subject);
		defaultAdder.add(boxChild);
		verify(subject).flow_back(boxChild);
	}

	@Test
	public void addInline()
	{
		DefaultAdder defaultAdder=new DefaultAdder(subject);
		defaultAdder.add(inlineChild);
		verify(subject).flow_back(inlineChild);
	}
}
