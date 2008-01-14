package com.beckproduct;

import java.util.Random;

import org.apache.camel.Exchange;
import org.apache.camel.processor.DelegateProcessor;
import org.apache.log4j.Logger;

public class ExceptionThrower extends DelegateProcessor
{
    private Logger logger = Logger.getLogger(this.getClass());

    protected void processNext(Exchange exchange) throws Exception
    {
        Random generator = new Random();
        int exception = generator.nextInt(3);

        try
        {
            switch (exception) {
            case 0:
                logger.info("No Exceptions here!");
                break;
            case 1:
                logger.warn("Whoa! That was close Mister!");
                break;
            case 2:
                throw new Exception("Boom! That's not good!");
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }
}
