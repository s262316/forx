package com.github.s262316.forx.css.util;

import java.util.HashMap;
import java.util.Map;

/**
 * i don't know what I was on when I wrote this back in 2006 or whenever, but it's
 * very clever and I have no idea how it works or how to use it anymore.
 * 
 * @param <XType>
 * @param <YType>
 */
public class InferenceTable<XType, YType>
{
    private int cols, rows;
    private boolean resultGrid[][];

    public InferenceTable(boolean resultGrid[][], int cols, int rows)
    {
        int i, j;

        this.cols=cols;
        this.rows=rows;

        this.resultGrid=new boolean[cols][rows];

        for(i=0; i<cols; i++)
        {
            for(j=0; j<rows; j++)
                this.resultGrid[i][j]=resultGrid[i][j];
        }
    }

    public Map<XType, YType> resolve(XType xValues[], YType yValues[])
    {
        int i, j;
        int match=0, item=0;
        boolean found=false;
        Map<XType, YType> mapping=new HashMap<XType, YType>();

        // look for properties with 1 possible value
        for(i=0; i<cols; i++)
        {
            for(j=0; j<rows; j++)
            {
                if(resultGrid[i][j]==true)
                {
                    match++;
                    item=j;
                }
            }

            if(match==1)
            {
                mapping.put(xValues[i], yValues[item]);
                zeroOutRowAndColumn(i, item);
            }
            match=0;
        }

        for(j=0; j<rows; j++)
        {
            for(i=0; i<cols; i++)
            {
                if(resultGrid[i][j]==true) //&& decs[i]==0)
                {
                    match++;
                    item=i;
                }
            }

            if(match==1)
            {
                mapping.put(xValues[item], yValues[j]);
                zeroOutRowAndColumn(item, j);
            }

            match=0;
        }

        for(i=0; i<cols; i++)
        {
//			if(decs[i]==0)
//			{
            found=false;
            for(j=0; j<rows && !found; )
            {
                if(resultGrid[i][j]==true)
                {
                    mapping.put(xValues[i], yValues[j]);
                    zeroOutRowAndColumn(i, j);
                }

                j++;
            }
//			}
        }

        return mapping;
    }

    public void zeroOutRowAndColumn(int i, int j)
    {
        int k;

        for(k=0; k<cols; k++)
                resultGrid[k][j]=false;

        for(k=0; k<rows; k++)
                resultGrid[i][k]=false;
    }
}

