package com.beckproduct.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.processor.DelegateProcessor;
import org.apache.log4j.Logger;

public class ExceptionChecker extends DelegateProcessor
{
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    protected void processNext(Exchange exchange) throws Exception
    {
        logger.info("Checking for exceptions");
    }
}
