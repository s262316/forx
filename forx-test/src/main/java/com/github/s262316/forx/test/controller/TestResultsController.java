package com.github.s262316.forx.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.s262316.forx.test.TestcaseResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.escape.Escapers;
import com.google.common.net.UrlEscapers;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TestResultsController
{
    private static Logger logger= LoggerFactory.getLogger(TestResultsController.class);

    @Value("${baseTestResultsFolder}")
    private File baseTestResultsFolder;
    @Value("${expectedResultsFolder}")
    private File firefoxResultsFolder;
    @Autowired
    private CssRefTestsExclusions autoTestExclusions;
    @Value("${cssTestSuiteFolder}")
    private Path cssTestSuiteFolder;
    @Autowired
    private Screenshots screenshots;
    @Autowired
    private AutomatedRefTests automatedRefTests;
    @Value("${manuallyVerifiedImagesFolder}")
    private File manuallyVerifiedFolder;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder)
    {
        dataBinder.setAutoGrowCollectionLimit(1000);
    }

    @RequestMapping("/submitResults")
    public String submitTestResults(TestResultsForm resultsForm) throws IOException
    {
        for(TestResultsEntry tre : resultsForm.getValue())
        {
            switch(tre.getResult())
            {
                case "ie":
                    Path p1=Paths.get(resultsForm.getFolderName()).resolve(tre.getTestcaseName());
                    Path p2=p1.subpath(1, p1.getNameCount());

                    // add the test to the ignore list
                    autoTestExclusions.addExclusion(StringUtils.replace(p2.toString(), "\\", "/"));
                    break;
                case "im":
                    Path p3=Paths.get(resultsForm.getFolderName()).resolve(tre.getTestcaseName());
                    Path p4=p3.subpath(1, p3.getNameCount());

                    // add the test to the ignore list
                    autoTestExclusions.addExclusion(StringUtils.replace(p4.toString(), "\\", "/"));

                    // move the actual to manually_verified/

                    Path screenshot=screenshots.getActualScreenshot(baseTestResultsFolder.toPath().resolve(p3));

                    Path screenshotDestination=manuallyVerifiedFolder.toPath().resolve(p4);
                    screenshotDestination=screenshotDestination.resolveSibling(screenshot.getName(screenshot.getNameCount()-1));
                    Files.createDirectories(screenshotDestination.getParent());


                    Files.copy(screenshot, screenshotDestination);

                    break;
                default:
                    break;
            }
        }

        return "redirect:/viewResults?folder="+UrlEscapers.urlFragmentEscaper().escape(resultsForm.getFolderName());
    }

    @RequestMapping("/")
    public ModelAndView testResultsMenuRoot()
    {
        Map<String, Object> model=new HashMap<>();

        File fs[]=baseTestResultsFolder.listFiles(File::isDirectory);
        List<File> fsl=Arrays.asList(fs);

        fsl.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));

        List<String> testResultNames=fsl.stream().map(File::getName).collect(Collectors.toList());

        model.put("thisFolder", "");
        model.put("runFolderResults", testResultNames);

        return new ModelAndView("menu", model);
    }

    @RequestMapping("/viewResults")
    public ModelAndView testResultsMenuSubFolder(@RequestParam("folder") String folder) throws IOException
    {
        Map<String, Object> model=new HashMap<>();
        File parentFolderFile;

        parentFolderFile=new File(baseTestResultsFolder, folder);

        File fs[]=parentFolderFile.listFiles(File::isDirectory);
        List<File> fsl=Arrays.asList(fs);

        fsl.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));

        Path pathBase=baseTestResultsFolder.toPath();

        List<String> testSubfolderNames=fsl.stream()
                .map(File::toPath)
                .map(v -> pathBase.relativize(v))
                .map(Path::toString)
                .collect(Collectors.toList());

        Path relPath=Paths.get(folder);
        Path p2;
        if(relPath.getNameCount()==1)
            p2=Paths.get("");
        else
            p2=Paths.get(folder).subpath(1, relPath.getNameCount());

        List<TestcaseResult> htmlTestCaseName=Files.list(cssTestSuiteFolder.resolve(p2))
                .filter(v -> v.toFile().isFile())
                .filter(v -> v.toString().endsWith(".xht") || v.toString().endsWith(".html"))
                .filter(v -> !v.toString().contains("-ref"))
                .map(this::mapTestcaseToResult)
                .collect(Collectors.toList());

        model.put("thisFolder", folder);
        model.put("runFolderResults", testSubfolderNames);
        model.put("testcaseResults", htmlTestCaseName);
        model.put("testcaseResultsModel", new MutableObject(htmlTestCaseName));

        return new ModelAndView("menu", model);
    }

    /**
     *
     * @param testResultImageRelative first part is the version string
     * @return
     */
    public TestcaseResult mapTestcaseToResult(Path testResultImageRelative)
    {
        try
        {
            String actualScreenshotUrl = null;
            String refScreenshotUrl = null;
            String actualFirefoxUrl = null;

            Path actualScreenshot = screenshots.getActualScreenshot(testResultImageRelative);
            if (actualScreenshot.toFile().exists())
                actualScreenshotUrl = convertToRelativeUrl(actualScreenshot);

            Path refScreenshot = screenshots.getRefScreenshot(testResultImageRelative);
            if (refScreenshot.toFile().exists())
                refScreenshotUrl = convertToRelativeUrl(refScreenshot);

            // firefox screenshot
            Path firefoxScreenshot = screenshots.getThirdPartyScreenshot(testResultImageRelative);
            if (firefoxScreenshot.toFile().exists())
                actualFirefoxUrl = convertToRelativeUrlFirefox(firefoxScreenshot);

            boolean isAutomatedRefTest = automatedRefTests.isAutomatedRefTest(testResultImageRelative);
            boolean isAutomatedRefTestIgnored=automatedRefTests.isAutomatedRefTestIgnored(testResultImageRelative);
            boolean isManuallyVerifiedTest=automatedRefTests.isManuallyVerifiedRefTest(testResultImageRelative);

            if(isAutomatedRefTest && !isAutomatedRefTestIgnored && refScreenshotUrl==null)
                logger.warn("automated ref test not found {}", refScreenshot);

            return new TestcaseResult(actualScreenshotUrl, refScreenshotUrl, actualFirefoxUrl,
                    testResultImageRelative.getName(testResultImageRelative.getNameCount() - 1).toString(), "",
                    isAutomatedRefTest,isAutomatedRefTestIgnored, isManuallyVerifiedTest);
        }
        catch(IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }

    public String convertToRelativeUrlFirefox(Path p)
    {
        Path relative=Paths.get("expected").resolve(firefoxResultsFolder.toPath().relativize(p));
        return StringUtils.replace(relative.toString(), "\\", "/");
    }

    public String convertToRelativeUrl(Path p)
    {
        Path relative=baseTestResultsFolder.toPath().relativize(p);
        return StringUtils.replace(relative.toString(), "\\", "/");
    }
}
