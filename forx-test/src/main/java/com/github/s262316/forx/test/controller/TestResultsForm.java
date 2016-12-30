package com.github.s262316.forx.test.controller;

import java.util.List;

public class TestResultsForm
{
    private String folderName;
    private List<TestResultsEntry> value;

    public String getFolderName()
    {
        return folderName;
    }

    public void setFolderName(String folderName)
    {
        this.folderName = folderName;
    }

    public List<TestResultsEntry> getValue()
    {
        return value;
    }

    public void setValue(List<TestResultsEntry> value)
    {
        this.value = value;
    }
}
