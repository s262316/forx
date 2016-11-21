/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.tree;

public class TreeException extends Exception
{
    private TreeExceptionType type;
    
    public TreeException(TreeExceptionType type)
    {
        this.type=type;
    }

}
