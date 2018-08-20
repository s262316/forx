package com.github.s262316.forx.reftests;

import com.google.common.collect.Iterators;
import org.apache.commons.io.FilenameUtils;
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

@Component
public class Screenshots
{
	@Value("${cssTestSuiteFolder}")
	private Path cssTestSuiteFolder;
	@Autowired
	private CssRefTestsITConfig config;
	private WebJarAssetLocator webJarLocator = new WebJarAssetLocator();

	public void saveActualScreenshot(Path htmlInputFile, BufferedImage image) throws IOException
	{
		Path screenshot=generateActualScreenshotFilename(htmlInputFile);
		Files.createDirectories(screenshot.getParent());
		ImageIO.write(image, "png", screenshot.toFile());
	}

	public void saveRefScreenshot(Path htmlInputFile, BufferedImage image) throws IOException
	{
		Path screenshot=generateRefScreenshotFilename(htmlInputFile);
		Files.createDirectories(screenshot.getParent());
		ImageIO.write(image, "png", screenshot.toFile());
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
			Path relativePath = htmlTestFile.relativize(cssTestSuiteFolder);
			webJarLocator.getFullPath("forx-test-ref-images", "manually-verified/" + relativePath.toString());
			return true;
		}
		catch(IllegalArgumentException iae)
		{
			return false;
		}

	}

}
