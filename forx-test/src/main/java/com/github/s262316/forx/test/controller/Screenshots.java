package com.github.s262316.forx.test.controller;

import com.github.s262316.forx.test.controller.ActualResultsLocation;
import com.google.common.collect.Iterators;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.webjars.WebJarAssetLocator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class Screenshots
{
	@Value("${cssTestSuiteFolder}")
	private Path cssTestSuiteFolder;
	@Value("${expectedResultsFolder}")
	private File thirdPartyResultsFolder;
	@Autowired
	private ActualResultsLocation config;
	private WebJarAssetLocator webJarLocator = new WebJarAssetLocator();

	public void saveActualScreenshot(Path htmlInputFile, BufferedImage image) throws IOException
	{
		Path screenshot=generateActualScreenshotFilename(htmlInputFile);
		Files.createDirectories(screenshot.getParent());
		ImageIO.write(image, "png", screenshot.toFile());
	}

	public Path getActualScreenshot(Path htmlInputFile)
	{
		Path screenshot=generateActualScreenshotFilename(htmlInputFile);
		return screenshot;
	}

	public void saveRefScreenshot(Path htmlInputFile, BufferedImage image) throws IOException
	{
		Path screenshot=generateRefScreenshotFilename(htmlInputFile);
		Files.createDirectories(screenshot.getParent());
		ImageIO.write(image, "png", screenshot.toFile());
	}

	public Path getRefScreenshot(Path htmlInputFile)
	{
		Path actualScreenshot=getActualScreenshot(htmlInputFile);
		String originalFilename=actualScreenshot.getName(actualScreenshot.getNameCount()-1).toString();

		String refFilename=FilenameUtils.getBaseName(originalFilename)+"_ref."+FilenameUtils.getExtension(originalFilename);
		return actualScreenshot.resolveSibling(refFilename);
	}

	private Path generateActualScreenshotFilename(Path htmlTestFile)
	{
		Path htmlTestFileRelative=cssTestSuiteFolder.relativize(htmlTestFile);
		Path screenshot=config.getActualResultsFolder().resolve(htmlTestFileRelative);

		String lastPart= Iterators.getLast(screenshot.iterator()).toString();
		String pngFilename= FilenameUtils.removeExtension(lastPart)+".png";
		screenshot=screenshot.resolveSibling(pngFilename);

		return screenshot;
	}

	private Path generateRefScreenshotFilename(Path htmlTestFile)
	{
		Path htmlTestFileRelative=cssTestSuiteFolder.relativize(htmlTestFile);
		Path screenshot=config.getActualResultsFolder().resolve(htmlTestFileRelative);

		String lastPart= Iterators.getLast(screenshot.iterator()).toString();
		String pngFilename=FilenameUtils.removeExtension(lastPart)+"_ref.png";
		screenshot=screenshot.resolveSibling(pngFilename);

		return screenshot;
	}

	public BufferedImage getManuallyVerifiedImage(Path htmlTestFile) throws IOException
	{
		Path relativePath=htmlTestFile.relativize(cssTestSuiteFolder);

		String fullpath=webJarLocator.getFullPath("forx-test-ref-images", "manually-verified/"+relativePath.toString());

		return ImageIO.read(new File(fullpath));
	}

	public boolean hasManuallyVerifiedImage(Path htmlTestFile) throws IOException
	{
		try
		{
			Path relativePath = cssTestSuiteFolder.relativize(htmlTestFile);
			// change extension to png
			String lastPart=relativePath.getName(relativePath.getNameCount()-1).toString();
			relativePath=relativePath.resolveSibling(FilenameUtils.getBaseName(lastPart)+".png");

			webJarLocator.getFullPath("forx-test-ref-images", "manually-verified/" +
					StringUtils.replace(relativePath.toString(), "\\", "/"));

			return true;
		}
		catch(IllegalArgumentException iae)
		{
			return false;
		}

	}

	public Path getThirdPartyScreenshot(Path testResultImageRelative)
	{
		Path relativeTestName=cssTestSuiteFolder.relativize(testResultImageRelative);
		String screenshotName=FilenameUtils.getBaseName(relativeTestName.toString())+".png";
		Path relativeScreenshot= relativeTestName.resolveSibling(screenshotName);

		Path absFirefoxscreenshot1=thirdPartyResultsFolder.toPath().resolve(relativeScreenshot);

		return absFirefoxscreenshot1;
	}
}
