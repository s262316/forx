package com.github.s262316.forx.css;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import com.github.s262316.forx.net.Resource;
import com.github.s262316.forx.tree.ReferringDocument;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.base.Optional;

class TestReferringDocument implements ReferringDocument
{
	@Override
	public Optional<Charset> getCharset()
	{
		return Optional.of(StandardCharsets.ISO_8859_1);
	}

	@Override
	public URL getLocation()
	{
		try
		{
			return new URL("http://www.google.com");
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}
}

public class TestCssLoader
{
	@Rule
	public WireMockRule server = new WireMockRule(8081);

	@Test
	public void testSmallerThanMaxCharset() throws Exception
	{
		server.stubFor(WireMock.get(urlEqualTo("/")).willReturn(aResponse().withBody("Test1")));

		TestReferringDocument referringDoc=new TestReferringDocument();
		CssLoader cssLoader=new CssLoader(() -> Optional.of(StandardCharsets.UTF_8));
		CssCharset cssCharset=new CssCharset();

		Resource resource=cssLoader.load("http://localhost:8081/", referringDoc);

		assertEquals(StandardCharsets.UTF_8, resource.getCharset());
		assertEquals(new URL("http://localhost:8081/"), resource.getUrl());
		assertEquals("Test1", IOUtils.toString(resource.getReader()));
		
	}

	@Test
	public void testNormalDoc() throws Exception
	{
		String testResource="Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1";
		server.stubFor(WireMock.get(urlEqualTo("/")).willReturn(aResponse().withBody(testResource)));

		TestReferringDocument referringDoc=new TestReferringDocument();
		CssLoader cssLoader=new CssLoader(() -> Optional.of(StandardCharsets.UTF_8));
		CssCharset cssCharset=new CssCharset();

		Resource resource=cssLoader.load("http://localhost:8081/", referringDoc);

		assertEquals(StandardCharsets.UTF_8, resource.getCharset());
		assertEquals(new URL("http://localhost:8081/"), resource.getUrl());
		assertEquals(testResource, IOUtils.toString(resource.getReader()));

	}

	@Test
	public void testCharsetFromCharsetRule() throws Exception
	{
		File cssFile=ResourceUtils.getFile("classpath:com/github/s262316/forx/css/at-charset-053.css");
		byte body[]=FileUtils.readFileToByteArray(cssFile);
		String testResource="@charset \"Shift-JIS\";";
		server.stubFor(WireMock.get(urlEqualTo("/")).willReturn(aResponse().withBody(testResource)));

		TestReferringDocument referringDoc=new TestReferringDocument();
		CssLoader cssLoader=new CssLoader(() -> Optional.of(StandardCharsets.UTF_8));
		CssCharset cssCharset=new CssCharset();

		Resource resource=cssLoader.load("http://localhost:8081/", referringDoc);

		assertEquals(Charset.forName("Shift_JIS"), resource.getCharset());
	}
}

