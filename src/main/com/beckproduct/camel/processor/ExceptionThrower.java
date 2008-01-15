package com.beckproduct.camel.processor;

import java.util.Random;

import org.apache.camel.Exchange;
import org.apache.camel.processor.DelegateProcessor;
import org.apache.log4j.Logger;

/**
 * This class randomly generates logging information
 * ranging from info to warn and error.
 * 
 * Note:  debug level info is not generated.
 * 
 * @author jbeck
 */
public class ExceptionThrower extends DelegateProcessor
{
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    protected void processNext(Exchange exchange) throws Exception
    {
        Random generator = new Random();
        int exception = generator.nextInt(3);

        try
        {
            switch (exception) {
            case 0:
                logger.info("logger.info");
                break;
            case 1:
                logger.warn("logger.warning");
                break;
            case 2:
                throw new Exception("logger.exception");
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }
}
