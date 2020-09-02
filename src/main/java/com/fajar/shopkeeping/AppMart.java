package com.fajar.shopkeeping;

import com.fajar.shopkeeping.handler.AppHandler;

/**
 * Hello world!
 *
 */ 
public class AppMart 
{
    public static void main( String[] args )
    {
      AppHandler appHandler = AppHandler.getInstance();
      appHandler.beginApp();
         
    }
}
