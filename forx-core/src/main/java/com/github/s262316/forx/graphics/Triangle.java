/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.graphics;

import java.awt.Polygon;


public class Triangle
{
    private Triangle()
    {}

    public static Polygon newTriangle(int x1, int y1, int x2, int y2, int x3, int y3)
    {
        int xs[]=new int[3];
        int ys[]=new int[3];

        xs[0]=x1;
        ys[0]=y1;
        xs[1]=x2;
        ys[1]=y2;
        xs[2]=x3;
        ys[2]=y3;

        return new Polygon(xs, ys, 3);
    }
}
