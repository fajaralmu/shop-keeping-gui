package com.fajar.shopkeeping;

import com.fajar.shopkeeping.handler.AppHandler;

/**
 * Hello world!
 *
 */ 
public class App 
{
    public static void main( String[] args )
    {
      AppHandler appHandler = AppHandler.getInstance();
      appHandler.beginApp();
         
    }
}
