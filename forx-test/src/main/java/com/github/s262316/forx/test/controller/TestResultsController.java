package com.github.s262316.forx.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.s262316.forx.test.TestcaseResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TestResultsController
{
    @Value("${baseTestResultsFolder}")
    private File baseTestResultsFolder;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder)
    {
        dataBinder.setAutoGrowCollectionLimit(1000);
    }

    @RequestMapping("/submitResults")
    public String submitTestResults(TestResultsForm resultsForm) throws IOException
    {
        ObjectMapper mapper=new ObjectMapper();

        File resultsFolder=new File(baseTestResultsFolder, resultsForm.getFolderName());
        File resultsFile=new File(resultsFolder, "results.json");

        mapper.writeValue(resultsFile, resultsForm);

        return "redirect:/viewResults?folder="+resultsForm.getFolderName();
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
        final List<String> testcaseFilenameExtensions= Lists.newArrayList("xhtml", "xht", "html");

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

        File html[]=parentFolderFile.listFiles(v -> v.isFile() && testcaseFilenameExtensions.contains(FilenameUtils.getExtension(v.getName())));
        List<File> htmlTestCaseFiles=Arrays.asList(html);

        List<TestcaseResult> htmlTestCaseName=htmlTestCaseFiles.stream()
                .map(File::toPath)
                .map(v -> pathBase.relativize(v))
                .map(Path::toString)
                .map(TestResultsController::mapTestcaseToResult)
                .collect(Collectors.toList());

        mergeExistingTestResults(htmlTestCaseName, folder);

        model.put("thisFolder", folder);
        model.put("runFolderResults", testSubfolderNames);
        model.put("testcaseResults", htmlTestCaseName);
        model.put("testcaseResultsModel", new MutableObject(htmlTestCaseName));

        return new ModelAndView("menu", model);
    }

    public void mergeExistingTestResults(List<TestcaseResult> testResults, String folderName) throws IOException
    {
        ObjectMapper mapper=new ObjectMapper();

        File resultsFolder=new File(baseTestResultsFolder, folderName);
        File resultsFile=new File(resultsFolder, "results.json");

        if(resultsFile.exists())
        {
            TestResultsForm existingResults = mapper.readValue(resultsFile, TestResultsForm.class);
            Map<String, TestResultsEntry> byName = Maps.uniqueIndex(existingResults.getValue(), v -> v.getTestcaseName());

            for (TestcaseResult testResult : testResults)
            {
                TestResultsEntry savedResult = byName.get(testResult.getTestcaseName());
                if (savedResult != null)
                    testResult.setResult(savedResult.getResult());
            }
        }
    }

    public static TestcaseResult mapTestcaseToResult(String htmlTestcaseName)
    {
        String actualScreenshotUrl=formulateActualFilename(htmlTestcaseName);
        String expectedScreenshotUrl=formulateExpectedFilename(htmlTestcaseName);

        return new TestcaseResult(actualScreenshotUrl, expectedScreenshotUrl, FilenameUtils.getName(htmlTestcaseName), "");
    }

    public static String formulateExpectedFilename(String testcaseHtmlFilename)
    {
        String filenameWithoutExtension=FilenameUtils.removeExtension(testcaseHtmlFilename);
//        String expectedFilename=filenameWithoutExtension+"_expected.png";
        String expectedFilename=filenameWithoutExtension+".png";

        return StringUtils.replace(expectedFilename, "\\", "/");
    }

    public static String formulateActualFilename(String testcaseHtmlFilename)
    {
        String filenameWithoutExtension=FilenameUtils.removeExtension(testcaseHtmlFilename);
        String expectedFilename=filenameWithoutExtension+"_actual.png";

        return StringUtils.replace(expectedFilename, "\\", "/");
    }

}
