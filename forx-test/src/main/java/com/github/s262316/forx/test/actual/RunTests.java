package com.github.s262316.forx.test.actual;

import com.github.s262316.forx.gui.WebView;
import com.google.common.collect.Iterators;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.PathEditor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication(scanBasePackages = {"com.github.s262316.forx"})
@Order(2)
@ConditionalOnProperty(name="run-actual")
public class RunTests implements CommandLineRunner
{
	private static Logger logger=LoggerFactory.getLogger(RunTests.class);

	private Path actualResultsFolder;
	@Value("${cssTestSuiteFolder}")
	private Path cssTestSuiteFolder;
	@Value("${cssTestsFilter}")
	private String cssTestsFilter;
	private AntPathMatcher pathMatcher=new AntPathMatcher();
	private int testsPerformed=0;
	@Autowired
	ApplicationContext applicationContext;

	@Value("${app.version}")
	String version;

	@Override
	public void run(String args[]) throws Exception
	{
		File htmlTestFile;
		int count=0;

		logger.info("run()");

		java.nio.file.Files.walk(cssTestSuiteFolder)
				.filter(v -> pathMatcher.match(this.cssTestsFilter, v.toString()))
				.forEach(v -> doTest(v));

		logger.info("performed {} tests", testsPerformed);
	}
	
	public void doTest(Path htmlTestFile)
	{
		try
		{
			Path screenshot=generateScreenshotFilename(htmlTestFile);
			Files.createDirectories(screenshot.getParent());
			runTest(htmlTestFile, screenshot);
		}
		catch(IOException ex)
		{
			logger.error("", ex);
			throw new RuntimeException(ex);
		}	
	}

	public void runTest(Path htmlInputFile, Path screenshotFile)
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
			
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(frame.getBounds());
	
			ImageIO.write(image, "png", screenshotFile.toFile());
				
			frame.setVisible(false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public Path generateScreenshotFilename(Path htmlTestFile)
	{
		Path htmlTestFileRelative=cssTestSuiteFolder.relativize(htmlTestFile);
		Path screenshot=actualResultsFolder.resolve(htmlTestFileRelative);

		String lastPart=Iterators.getLast(screenshot.iterator()).toString();
		String pngFilename=FilenameUtils.removeExtension(lastPart)+".png";
		screenshot=screenshot.resolveSibling(pngFilename);

		return screenshot;
	}

	@Value("${actualResultsFolder}")
	public void setActualResultsFolder(Path actualResultsFolder)
	{
		this.actualResultsFolder=actualResultsFolder.resolve(
				version+"_"+ DateTimeFormatter.ofPattern("yyyyMMdd_HHmm").format(LocalDateTime.now()));
	}

	public static void main(String args[])
	{
		SpringApplication app=new SpringApplication(RunTests.class);
		app.setWebEnvironment(false);
		app.run(args);
	}
}
