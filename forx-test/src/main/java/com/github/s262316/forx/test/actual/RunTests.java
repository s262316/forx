package com.github.s262316.forx.test.actual;

import com.github.s262316.forx.gui.WebView;
import com.github.s262316.forx.test.expected.GenerateExpected;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import javafx.application.Application;
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
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication(scanBasePackages = {"com/github/s262316/forx"})
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
	    	WebView webView = applicationContext.getBean(WebView.class);
	    	webView.tempLocationMoveMe=htmlInputFile.toString();
			JFrame frame=new JFrame(htmlInputFile.toString());

			frame.getContentPane().add(webView);
			frame.setSize(400, 400);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);

			logger.info("testing {}", htmlInputFile);
			webView.load(htmlInputFile.toString());
			
			Thread.sleep(1000);
			
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(frame.getBounds());
	
			ImageIO.write(image, "png", screenshotFile.toFile());
				
			frame.setVisible(false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
		app.setWebEnvironment(true);
		app.run(args);
	}
}
