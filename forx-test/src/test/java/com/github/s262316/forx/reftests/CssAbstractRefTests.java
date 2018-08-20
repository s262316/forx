package com.github.s262316.forx.reftests;

import com.github.s262316.forx.gui.WebView;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public abstract class CssAbstractRefTests
{
	private static Logger logger= LoggerFactory.getLogger(CssAbstractRefTests.class);

	@Autowired
	ApplicationContext applicationContext;
	@Value("${cssTestSuiteFolder}")
	private Path cssTestSuiteFolder;

	public DynamicNode convertToDynamicTest(Path p)
	{
		logger.info("convertToDynamicTest {}", p);

		try
		{
			if (p.toFile().isDirectory())
			{
				return dynamicContainer(p.getName(p.getNameCount()-1).toString(),
							Files.list(p)
									.filter(this::canExecuteTest)
									.map(v -> convertToDynamicTest(v)));
			}
			else
			{
				return dynamicTest(p.getName(p.getNameCount()-1).toString(), () -> doTest(p));
			}
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}

	public static Path getAutomatedRefFile(Path htmlFile) throws IOException
	{
		Document doc = Jsoup.parse(htmlFile.toFile(), "UTF-8", "http://example.com/");
		String matchFile=doc.select("link[rel='match']").attr("href");

		if(StringUtils.isBlank(matchFile))
			return null;

		return htmlFile.resolveSibling(matchFile);
	}

	public BufferedImage runTest(Path htmlInputFile)
	{
		try
		{
			// create a url
			String relativePath=cssTestSuiteFolder.relativize(htmlInputFile).toString();

			String url="http://localhost:8080/testcases/"+
					StringUtils.replace(relativePath, "\\", "/");

			logger.info("testing {}", url);

			WebView webView = applicationContext.getBean(WebView.class);
			webView.tempLocationMoveMe=url;
			JFrame frame=new JFrame(url);

			frame.getContentPane().add(webView);
			frame.setSize(400, 400);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);

			webView.load(url);

			Thread.sleep(1000);

			BufferedImage image = new BufferedImage(
					webView.getWidth(),
					webView.getHeight(),
					BufferedImage.TYPE_INT_ARGB
			);

			frame.getContentPane().paint( image.getGraphics() );

			frame.setVisible(false);

			return image;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public abstract boolean canExecuteTest(Path path);

	public abstract void doTest(Path p);
}
