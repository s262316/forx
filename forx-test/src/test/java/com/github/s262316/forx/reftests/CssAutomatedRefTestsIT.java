package com.github.s262316.forx.reftests;

import com.github.s262316.forx.gui.WebView;
import com.google.common.collect.Iterators;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.PathEditor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.AntPathMatcher;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

// VM argument -Djava.awt.headless=false
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=CssRefTestsITConfig.class)
public class CssAutomatedRefTestsIT
{
	private static Logger logger= LoggerFactory.getLogger(CssAutomatedRefTestsIT.class);

	/** see setActualResultsFolder() */
	private Path actualResultsFolder;
	@Value("${cssTestSuiteFolder}")
	private Path cssTestSuiteFolder;
	@Value("${cssTestsFilter}")
	private String[] cssTestsFilter;
	private AntPathMatcher pathMatcher=new AntPathMatcher();
	@Autowired
	ApplicationContext applicationContext;
	@Value("${app.version}")
	String version;

	public Path refMatch(Path htmlFile) throws IOException
	{
		Document doc = Jsoup.parse(htmlFile.toFile(), "UTF-8", "http://example.com/");
		String matchFile=doc.select("link[rel='match']").attr("href");

		if(StringUtils.isBlank(matchFile))
			return null;

		return htmlFile.resolveSibling(matchFile);
	}

	public boolean pathMatches(Path p)
	{
		try
		{
			if (p.toFile().isDirectory())
			{
				boolean r = Arrays.stream(cssTestsFilter)
						.anyMatch(v -> pathMatcher.match(v, p.toString()) || pathMatcher.matchStart(v, p.toString()));

				return r;
			}
			else
			{
				return !FilenameUtils.getBaseName(p.toString()).contains("-ref") &&
						refMatch(p)!=null &&
						FilenameUtils.isExtension(p.toString(), new String[] { "xht", "html" });
			}
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}

	DynamicNode convertToDynamicTest(Path p)
	{
		logger.info("path {}", p);

		try
		{
			if (p.toFile().isDirectory())
			{
				return dynamicContainer(p.getName(p.getNameCount()-1).toString(),
							Files.list(p)
									.filter(this::pathMatches)
									.map(v -> convertToDynamicTest(v)));
			}
			else
			{
				return dynamicTest(p.getName(p.getNameCount()-1).toString(), () -> doTest(p, refMatch(p)));
			}
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}

	@TestFactory
	public Stream<DynamicNode> automatedRefTests() throws Exception
	{
		return Files.list(cssTestSuiteFolder)
				.filter(this::pathMatches)
				.map(v -> convertToDynamicTest(v))
				;
	}

	@Value("${actualResultsFolder}")
	public void setActualResultsFolder(Path actualResultsFolder)
	{
		this.actualResultsFolder=actualResultsFolder.resolve(
				version+"_"+ DateTimeFormatter.ofPattern("yyyyMMdd_HHmm").format(LocalDateTime.now()));
	}

	public void doTest(Path htmlTestFile, Path refHtmlFile)
	{
		try
		{
			Path screenshot=generateScreenshotFilename(htmlTestFile);
			Files.createDirectories(screenshot.getParent());
			BufferedImage b1=runTest(htmlTestFile, screenshot);

			Path refScreenshot=generateRefScreenshotFilename(htmlTestFile);
			BufferedImage b2=runTest(refHtmlFile, refScreenshot);

			assertTrue(compareImages(b1, b2), "screenshots mismatched");
			assertFalse(isAllBackground(b1, UIManager.getColor ( "Panel.background" )), "nothing shown in screenshot");

		}
		catch(IOException ex)
		{
			logger.error("", ex);
			throw new RuntimeException(ex);
		}
	}

	public Path generateScreenshotFilename(Path htmlTestFile)
	{
		Path htmlTestFileRelative=cssTestSuiteFolder.relativize(htmlTestFile);
		Path screenshot=actualResultsFolder.resolve(htmlTestFileRelative);

		String lastPart= Iterators.getLast(screenshot.iterator()).toString();
		String pngFilename=FilenameUtils.removeExtension(lastPart)+".png";
		screenshot=screenshot.resolveSibling(pngFilename);

		return screenshot;
	}

	public Path generateRefScreenshotFilename(Path htmlTestFile)
	{
		Path htmlTestFileRelative=cssTestSuiteFolder.relativize(htmlTestFile);
		Path screenshot=actualResultsFolder.resolve(htmlTestFileRelative);

		String lastPart= Iterators.getLast(screenshot.iterator()).toString();
		String pngFilename=FilenameUtils.removeExtension(lastPart)+"_ref.png";
		screenshot=screenshot.resolveSibling(pngFilename);

		return screenshot;
	}

	public BufferedImage runTest(Path htmlInputFile, Path screenshotFile)
	{
		PathEditor p;

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

			ImageIO.write(image, "png", screenshotFile.toFile());

			frame.setVisible(false);

			return image;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Compares two images pixel by pixel.
	 *
	 * @param imgA the first image.
	 * @param imgB the second image.
	 * @return whether the images are both the same or not.
	 */
	public static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
		// The images must be the same size.
		if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
			return false;
		}

		int width  = imgA.getWidth();
		int height = imgA.getHeight();

		// Loop over every pixel.
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// Compare the pixels for equality.
				if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean isAllBackground(BufferedImage image, Color background)
	{
		int width  = image.getWidth();
		int height = image.getHeight();

		// Loop over every pixel.
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// Compare the pixels for equality.
				if (image.getRGB(x, y) != background.getRGB()) {
					return false;
				}
			}
		}

		return true;
	}
}
