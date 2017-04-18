package com.github.s262316.forx.tree.visual;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class XmlVElementTest
{
    @Test
    public void findCounterIsPresentLocally()
    {
        XmlVElement element = new XmlVElement("div", null, 0, null, null, null);
        element.reset_counter("mycounter", 22);

        VElement location = element.find_counter("mycounter");
        assertSame(element, location);
    }

    @Test
    public void findCounterIsPresentInParent()
    {
        XmlVElement parent=mock(XmlVElement.class);
        when(parent.find_counter("mycounter")).thenReturn(parent);

        XmlVElement element = new XmlVElement("div", null, 0, null, null, null);
        element.setParentNode(parent);

        VElement location = element.find_counter("mycounter");
        assertSame(parent, location);
    }

    @Test
    public void findCounterIsNotPresent()
    {
        XmlVElement element = new XmlVElement("div", null, 0, null, null, null);
        VElement location = element.find_counter("mycounter");
        assertNull(location);
    }
}
