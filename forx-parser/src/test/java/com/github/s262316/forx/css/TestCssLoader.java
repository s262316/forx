package com.github.s262316.forx.css;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import com.github.s262316.forx.tree.resource.Resource;
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
		server.stubFor(WireMock.get(WireMock.urlEqualTo("/")).willReturn(WireMock.aResponse().withBody("Test1")));

		TestReferringDocument referringDoc=new TestReferringDocument();
		CssLoader cssLoader=new CssLoader(() -> Optional.of(StandardCharsets.UTF_8));
		CssCharset cssCharset=new CssCharset();

		Resource resource=cssLoader.load("http://localhost:8081/", referringDoc);

		Assert.assertEquals(StandardCharsets.UTF_8, resource.getCharset());
		Assert.assertEquals(new URL("http://localhost:8081/"), resource.getUrl());
		Assert.assertEquals("Test1", IOUtils.toString(resource.getReader()));
		
	}

	@Test
	public void testNormalDoc() throws Exception
	{
		String testResource="Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1";
		server.stubFor(WireMock.get(WireMock.urlEqualTo("/")).willReturn(WireMock.aResponse().withBody(testResource)));

		TestReferringDocument referringDoc=new TestReferringDocument();
		CssLoader cssLoader=new CssLoader(() -> Optional.of(StandardCharsets.UTF_8));
		CssCharset cssCharset=new CssCharset();

		Resource resource=cssLoader.load("http://localhost:8081/", referringDoc);

		Assert.assertEquals(StandardCharsets.UTF_8, resource.getCharset());
		Assert.assertEquals(new URL("http://localhost:8081/"), resource.getUrl());
		Assert.assertEquals(testResource, IOUtils.toString(resource.getReader()));

	}

	@Test
	public void testCharsetFromCharsetRule() throws Exception
	{
		File cssFile=ResourceUtils.getFile("classpath:com/github/s262316/forx/com.github.s262316.forx.css/at-charset-053.com.github.s262316.forx.css");
		byte body[]=FileUtils.readFileToByteArray(cssFile);
		String testResource="@charset \"Shift-JIS\";";
		server.stubFor(WireMock.get(WireMock.urlEqualTo("/")).willReturn(WireMock.aResponse().withBody(testResource)));

		TestReferringDocument referringDoc=new TestReferringDocument();
		CssLoader cssLoader=new CssLoader(() -> Optional.of(StandardCharsets.UTF_8));
		CssCharset cssCharset=new CssCharset();

		Resource resource=cssLoader.load("http://localhost:8081/", referringDoc);

		Assert.assertEquals(Charset.forName("Shift_JIS"), resource.getCharset());
	}

	@Test
	public void bomIsNotInTheStream() throws Exception
	{
		File cssFile=ResourceUtils.getFile("classpath:com/github/s262316/forx/com.github.s262316.forx.css/utf16lebom.com.github.s262316.forx.css");
		byte body[]=FileUtils.readFileToByteArray(cssFile);
		server.stubFor(WireMock.get(WireMock.urlEqualTo("/")).willReturn(WireMock.aResponse().withBody(body)));

		TestReferringDocument referringDoc=new TestReferringDocument();
		CssLoader cssLoader=new CssLoader(() -> Optional.of(StandardCharsets.UTF_8));

		Resource resource=cssLoader.load("http://localhost:8081/", referringDoc);

		String decodedCss=IOUtils.toString(resource.getReader());
		MatcherAssert.assertThat(decodedCss, Matchers.startsWith("@"));
	}
}




