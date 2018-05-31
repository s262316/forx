package com.github.s262316.forx.util;

public class Objects2
{
	/**
	 * returns first non-zero, or zero if they're all 0
	 * 
	 * @param nums
	 * @return
	 */
	public static int firstNonZero(int... nums)
	{
		for(int n : nums)
		{
			if(n!=0)
				return n;
		}
		
		return 0;
	}
}
