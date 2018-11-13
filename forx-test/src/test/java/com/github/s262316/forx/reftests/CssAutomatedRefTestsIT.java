package com.github.s262316.forx.reftests;

import com.github.s262316.forx.test.controller.CssRefTestsExclusions;
import com.github.s262316.forx.test.controller.Screenshots;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.AntPathMatcher;

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
public class CssAutomatedRefTestsIT extends CssAbstractRefTests
{
	private static Logger logger= LoggerFactory.getLogger(CssAutomatedRefTestsIT.class);

	@Autowired
	CssRefTestsITConfig config;
	@Value("${cssTestSuiteFolder}")
	private Path cssTestSuiteFolder;
	@Value("${cssTestsFilter}")
	private String[] cssTestsFilter;
	private AntPathMatcher pathMatcher=new AntPathMatcher();
	@Autowired
	CssRefTestsExclusions exclusions;
	@Autowired
	Screenshots screenshots;

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
				if(exclusions.isExcluded(cssTestSuiteFolder, p))
				{
					logger.warn("automated test {} is excluded", p);
					return false;
				}

				return !FilenameUtils.getBaseName(p.toString()).contains("-ref") &&
						getAutomatedRefFile(p)!=null &&
						FilenameUtils.isExtension(p.toString(), new String[] { "xht", "html" });
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
				.filter(this::canExecuteTest)
				.map(v -> convertToDynamicTest(v))
				;
	}

	@Override
	public void doTest(Path htmlTestFile)
	{
		try
		{
			BufferedImage b1=runTest(htmlTestFile);
			screenshots.saveActualScreenshot(htmlTestFile, b1);

			Path refHtmlFile=getAutomatedRefFile(htmlTestFile);
			BufferedImage b2=runTest(refHtmlFile);
			screenshots.saveRefScreenshot(htmlTestFile, b2);

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
