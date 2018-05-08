package com.github.s262316.forx.test.expected;

import com.github.s262316.forx.test.ForxTest;

import com.google.common.collect.Iterators;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootApplication
@Order(1)
@ConditionalOnProperty(name="generated-expected")
public class GenerateExpected implements CommandLineRunner
{
    private static Logger logger= LoggerFactory.getLogger(GenerateExpected.class);

    @Value("${expectedResultsFolder}")
    private Path expectedResultsFolder;
    @Value("${cssTestSuiteFolder}")
    private Path cssTestSuiteFolder;
    private ChromeDriver driver;
    //	private InternetExplorerDriver driver;
//    private FirefoxDriver driver;
//    private FirefoxProfile firefoxProfile;
    private AntPathMatcher pathMatcher=new AntPathMatcher();
    @Value("${cssTestsFilter}")
    private String cssTestsFilter;
    private int testsPerformed=0;

    @PostConstruct
    public void init()
    {
//        FirefoxOptions options=new FirefoxOptions();
//        options.setLogLevel(FirefoxDriverLogLevel.TRACE);
//        driver = new FirefoxDriver(options);
        driver=new ChromeDriver();

        // this implicit wait will invisibly wait for elements to become loaded
        // while we're requesting them
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.manage().window().setSize(new Dimension(400, 400));
    }

    @Override
    public void run(String args[]) throws Exception
    {
        logger.info("run()");

        List<Path> allFiles=Files.walk(cssTestSuiteFolder)
                .filter(v -> pathMatcher.match(this.cssTestsFilter, v.toString()))
                .collect(Collectors.toList());

        for(Path v : allFiles)
            doTest(v);

        logger.info("performed {} tests", testsPerformed);
    }

    public void doTest(Path htmlTestFile)
    {
        logger.info("doTest({})", "file:"+htmlTestFile.toString());

        try
        {
            driver.get("file:"+htmlTestFile.toString());
            Path temp=driver.getScreenshotAs(OutputType.FILE).toPath();

            Path htmlTestFileRelative=cssTestSuiteFolder.relativize(htmlTestFile);
            Path dest=expectedResultsFolder.resolve(htmlTestFileRelative);

            String lastPart=Iterators.getLast(dest.iterator()).toString();
            String pngFilename=FilenameUtils.removeExtension(lastPart)+".png";
            dest=dest.resolveSibling(pngFilename);

            logger.debug("copying from {} to {}", temp, dest);

            Files.createDirectories(dest.getParent());
            Files.copy(temp, dest);

            testsPerformed++;
        }
        catch(Exception ex)
        {
            logger.error("", ex);
            throw new RuntimeException(ex);
        }
    }

    public static void main(String args[])
    {
        SpringApplication app=new SpringApplication(GenerateExpected.class);
        app.setWebEnvironment(true);
        app.run(args);
    }
}
