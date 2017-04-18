package com.github.s262316.forx.tree.visual;

import com.github.s262316.forx.style.ValueBuilder;
import com.github.s262316.forx.style.ValueList;
import com.github.s262316.forx.tree.visual.util.XmlVNodes;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(XmlVNodes.class)
public class PseudoElementTest
{
    @Mock
    XmlVElement body;
    @Mock
    XmlVElement p1;
    @Mock
    XmlVElement p2;
    @Mock
    XmlVElement p3;
    @Mock
    XmlVElement missing;

    @Rule
    public ExpectedException thrown=ExpectedException.none();

    @Before
    public void setup()
    {
        when(body.counter_value("mycounter")).thenReturn(Optional.of(1));
        when(p1.counter_value("mycounter")).thenReturn(Optional.of(2));
        when(p2.counter_value("mycounter")).thenReturn(Optional.of(3));
        when(p3.counter_value("mycounter")).thenReturn(Optional.of(4));
        when(missing.counter_value("mycounter")).thenReturn(Optional.empty());
    }

    @Test
    public void allLevelsHaveTheCounter()
    {
        PowerMockito.mockStatic(XmlVNodes.class);
        when(XmlVNodes.pathToHere(any(XmlVElement.class))).thenReturn(Lists.newArrayList(body, p1, p2, p3));

        ValueList list=new ValueBuilder()
                .identifier("mycounter")
                .string(".")
                .buildAsList();

        PseudoElement element=new PseudoElement(p3, null, null);

        String formattedResult=element.counters_function(list);
        assertEquals("1.2.3.4", formattedResult);
    }

    @Test
    public void oneLevelHasMissingCounter()
    {
        PowerMockito.mockStatic(XmlVNodes.class);
        when(XmlVNodes.pathToHere(any(XmlVElement.class))).thenReturn(Lists.newArrayList(body, p1, missing, p3));

        ValueList list=new ValueBuilder()
                .identifier("mycounter")
                .string(".")
                .buildAsList();

        PseudoElement element=new PseudoElement(p3, null, null);

        String formattedResult=element.counters_function(list);
        assertEquals("1.2.4", formattedResult);
    }

    @Test
    public void withListStyle()
    {
        PowerMockito.mockStatic(XmlVNodes.class);
        when(XmlVNodes.pathToHere(any(XmlVElement.class))).thenReturn(Lists.newArrayList(body, p1, missing, p3));

        ValueList list=new ValueBuilder()
                .identifier("mycounter")
                .string(".")
                .identifier("lower-roman")
                .buildAsList();

        PseudoElement element=new PseudoElement(p3, null, null);

        String formattedResult=element.counters_function(list);
        assertEquals("i.ii.iv", formattedResult);
    }

    @Test
    public void withInvalidCounter()
    {
        thrown.expect(CounterFunctionMalformedException.class);

        ValueList list=new ValueBuilder()
                .string("mycounter")
                .string(".")
                .identifier("lower-roman")
                .buildAsList();

        PseudoElement element=new PseudoElement(p3, null, null);
        element.counters_function(list);
    }

    @Test
    public void withInvalidSeparator()
    {
        thrown.expect(CounterFunctionMalformedException.class);

        ValueList list=new ValueBuilder()
                .identifier("mycounter")
                .identifier(".")
                .identifier("lower-roman")
                .buildAsList();

        PseudoElement element=new PseudoElement(p3, null, null);
        element.counters_function(list);
    }
}
