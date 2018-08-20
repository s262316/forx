package com.github.s262316.forx.reftests;

import com.github.s262316.forx.gui.WebView;
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
import org.webjars.WebJarAssetLocator;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

// VM argument -Djava.awt.headless=false
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=CssRefTestsITConfig.class)
public class CssManuallyVerifiedRefTestsIT extends CssAbstractRefTests
{
	private static Logger logger= LoggerFactory.getLogger(CssManuallyVerifiedRefTestsIT.class);

	@Autowired
	CssRefTestsITConfig cssRefTestsITConfig;
	@Value("${cssTestSuiteFolder}")
	private Path cssTestSuiteFolder;
	@Value("${cssTestsFilter}")
	private String[] cssTestsFilter;
	@Autowired
	private ApplicationContext applicationContext;
	@Value("${app.version}")
	private String version;
	@Autowired
	private CssRefTestsExclusions exclusions;
	@Autowired
	private Screenshots screenshots;

	private AntPathMatcher pathMatcher=new AntPathMatcher();

	@Override
	public boolean canExecuteTest(Path p)
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
				boolean autoExcluded=exclusions.isExcluded(cssTestSuiteFolder, p);
				boolean notRefFile=!FilenameUtils.getBaseName(p.toString()).contains("-ref");
				boolean isHtmlFile=FilenameUtils.isExtension(p.toString(), new String[] { "xht", "html" });
				boolean hasAutomatedRef= getAutomatedRefFile(p)!=null;
				boolean manuallyVerifiedScreenshot=screenshots.hasManuallyVerifiedImage(p);

				if(autoExcluded && !manuallyVerifiedScreenshot)
					logger.warn("An auto-exclusion was found but there is no manually-verified screenshot for {}", p);

				return isHtmlFile && notRefFile &&
						manuallyVerifiedScreenshot &&
						(
						!hasAutomatedRef ||
						(hasAutomatedRef && autoExcluded)
						);
			}
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}

	@TestFactory
	public Stream<DynamicNode> verifiedManuallyRefTests() throws Exception
	{
		return Files.list(cssTestSuiteFolder)
				.filter(this::canExecuteTest)
				.map(v -> convertToDynamicTest(v));
	}

	@Override
	public void doTest(Path htmlTestFile)
	{
		try
		{
			BufferedImage b1=runTest(htmlTestFile);
			screenshots.saveActualScreenshot(htmlTestFile, b1);

			BufferedImage b2=screenshots.getManuallyVerifiedImage(htmlTestFile);

			assertTrue(ImageUtils.compareImages(b1, b2), "screenshots mismatched");
			assertFalse(ImageUtils.isAllBackground(b1, UIManager.getColor ( "Panel.background" )), "nothing shown in screenshot");
		}
		catch(IOException ex)
		{
			logger.error("", ex);
			throw new RuntimeException(ex);
		}
	}
}
