package com.github.s262316.forx.tree.visual;

import com.github.s262316.forx.tree.events2.MutationType;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;
import com.github.s262316.forx.tree.impl.XmlDocument;
import com.github.s262316.forx.tree.style.Stylesheet;
import com.google.common.base.Optional;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TestLinkElementHandler
{
    @Test
    public void adddingLinkToConnectedTreeCausesStylesheetToBeMerged() throws Exception
    {
        URL url=ResourceUtils.getFile("classpath:com/github/s262316/forx/css/simple.css").toURI().toURL();

        XmlDocument doc=Mockito.mock(XmlDocument.class);
        when(doc.getCharset()).thenReturn(Optional.of(StandardCharsets.UTF_8));
        XmlVElement spyElement=new XmlVElement("link", doc, 0, null, null, null);
        spyElement.setAttr("href", url.toString());
        spyElement.setAttr("charset", "utf-8");
        XmlVElement element=Mockito.spy(spyElement);
        LinkElementHandler listener=new LinkElementHandler(element, null);

        ReflectionTestUtils.setField(listener, "connected", true);

        listener.added(new XmlMutationEvent(MutationType.ADD, element));

        verify(doc, times(1)).mergeStyles(any(XmlVElement.class), any(Stylesheet.class));
    }

    @Test
    public void adddingLinkToConnectedTreeCausesStylesheetProcessedButNotMerged() throws Exception
    {
        URL url=ResourceUtils.getFile("classpath:com/github/s262316/forx/css/simple.css").toURI().toURL();

        XmlDocument doc=Mockito.mock(XmlDocument.class);
        when(doc.getCharset()).thenReturn(Optional.of(StandardCharsets.UTF_8));
        XmlVElement spyElement=new XmlVElement("link", doc, 0, null, null, null);
        spyElement.setAttr("href", url.toString());
        spyElement.setAttr("charset", "utf-8");
        XmlVElement element=Mockito.spy(spyElement);
        LinkElementHandler listener=new LinkElementHandler(element, null);

        listener.added(new XmlMutationEvent(MutationType.ADD, element));
        verify(doc, never()).demergeStylesFrom(any(XmlVElement.class));
        verify(doc, never()).mergeStyles(any(XmlVElement.class), any(Stylesheet.class));
        assertNotNull(ReflectionTestUtils.getField(listener, "linkElementStylesheet"));
    }

    @Test
    public void disconnectingLinkCausesStylesheetToBeDemerged()
    {
        XmlDocument doc=Mockito.mock(XmlDocument.class);
        when(doc.getCharset()).thenReturn(Optional.of(StandardCharsets.UTF_8));
        XmlVElement element=Mockito.spy(new XmlVElement("link", doc, 0, null, null, null));
        LinkElementHandler listener=new LinkElementHandler(element, null);

        ReflectionTestUtils.setField(listener, "connected", true);
        ReflectionTestUtils.setField(listener, "linkElementStylesheet", new Stylesheet(null, null));

        listener.disconnected(new XmlMutationEvent(MutationType.ADD, element));

        verify(doc, times(1)).demergeStylesFrom(any(XmlVElement.class));
        verify(doc, never()).mergeStyles(any(XmlVElement.class), any(Stylesheet.class));
        assertNull(ReflectionTestUtils.getField(listener, "linkElementStylesheet"));
    }

    @Test
    public void connectLinkToCausesStylesheetToBeMerged() throws Exception
    {
        URL url=ResourceUtils.getFile("classpath:com/github/s262316/forx/css/simple.css").toURI().toURL();

        XmlDocument doc=Mockito.mock(XmlDocument.class);
        when(doc.getCharset()).thenReturn(Optional.of(StandardCharsets.UTF_8));
        XmlVElement spyElement=new XmlVElement("link", doc, 0, null, null, null);
        spyElement.setAttr("href", url.toString());
        spyElement.setAttr("charset", "utf-8");
        XmlVElement element=Mockito.spy(spyElement);
        LinkElementHandler listener=new LinkElementHandler(element, null);

        Stylesheet ss=new Stylesheet(null, null);
        ReflectionTestUtils.setField(listener, "linkElementStylesheet", ss);

        listener.connected(new XmlMutationEvent(MutationType.ADD, element));

        verify(doc, times(1)).mergeStyles(same(element), same(ss));
    }
}
