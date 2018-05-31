package com.github.s262316.forx.diagn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;

import com.github.s262316.forx.box.RootBox;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BoxDrivenDiagnostics
{
	public static void output(RootBox rootBox)
	{
		try
		{
			List<String> myMappingFiles = new ArrayList<>();
			myMappingFiles.add("com/github/s262316/forx/core/diagn/boxmapping1.xml");
			DozerBeanMapper mapper = new DozerBeanMapper();
			mapper.setMappingFiles(myMappingFiles);
			
			MyNode destObject = mapper.map(rootBox, MyNode.class);
	
			ObjectMapper mapper1 = new ObjectMapper();

			mapper1.writeValue(new File(System.currentTimeMillis()+".log"), destObject);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
