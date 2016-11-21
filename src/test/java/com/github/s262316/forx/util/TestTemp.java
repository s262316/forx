package com.github.s262316.forx.util;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;

public class TestTemp
{
	public static void main(String args[])
	{
		try
		{
			List<String> myMappingFiles = new ArrayList<>();
			myMappingFiles.add("com/github/s262316/forx/util/boxmapping1.xml");
			DozerBeanMapper mapper = new DozerBeanMapper();
			mapper.setMappingFiles(myMappingFiles);

			X x=new X();
			x.setA(12345);
			x.setB("aaaaa");
			
			Y destObject = mapper.map(x, Y.class);
	
			System.out.println(destObject.getB());
			System.out.println(destObject.getMymap());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
