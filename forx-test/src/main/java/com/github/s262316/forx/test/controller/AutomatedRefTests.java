package com.github.s262316.forx.test.controller;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.font.ScriptRun;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class AutomatedRefTests
{
	private static Logger logger= LoggerFactory.getLogger(AutomatedRefTests.class);

	@Autowired
	CssRefTestsExclusions exclusions;
	@Autowired
	Screenshots screenshots;
	@Value("${cssTestSuiteFolder}")
	private Path cssTestSuiteFolder;

	public boolean isAutomatedRefTest(Path absoluteTestPath) throws IOException
	{
		Path p=cssTestSuiteFolder.relativize(absoluteTestPath);

		if(exclusions.isExcluded(Paths.get(""), p))
		{
			return false;
		}

		return !FilenameUtils.getBaseName(absoluteTestPath.toString()).contains("-ref") &&
				getAutomatedRefFile(absoluteTestPath)!=null &&
				FilenameUtils.isExtension(absoluteTestPath.toString(), new String[] { "xht", "html" });
	}

	public boolean isAutomatedRefTestIgnored(Path absoluteTestPath) throws IOException
	{
		Path p=cssTestSuiteFolder.relativize(absoluteTestPath);

		return exclusions.isExcluded(Paths.get(""), p);
	}

	public static Path getAutomatedRefFile(Path htmlFile) throws IOException
	{
		Document doc = Jsoup.parse(htmlFile.toFile(), "UTF-8", "http://example.com/");
		String matchFile=doc.select("link[rel='match']").attr("href");

		if(StringUtils.isBlank(matchFile))
			return null;

		return htmlFile.resolveSibling(matchFile);
	}

	public boolean isManuallyVerifiedRefTest(Path absoluteTestPath) throws IOException
	{
		Path p=cssTestSuiteFolder.relativize(absoluteTestPath);

		boolean autoExcluded=exclusions.isExcluded(Paths.get(""), p);
		boolean hasAutomatedRef= getAutomatedRefFile(absoluteTestPath)!=null;



		boolean manuallyVerifiedScreenshot=screenshots.hasManuallyVerifiedImage(absoluteTestPath);

		return manuallyVerifiedScreenshot &&
				(
						!hasAutomatedRef ||
								(hasAutomatedRef && autoExcluded)
				);
	}
}
