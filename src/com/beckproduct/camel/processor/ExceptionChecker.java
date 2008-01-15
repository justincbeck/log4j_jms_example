package com.beckproduct.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.processor.DelegateProcessor;
import org.apache.log4j.Logger;

import com.beckproduct.repository.ILogEntryRepository;

public class ExceptionChecker extends DelegateProcessor
{
    private Logger logger = Logger.getLogger(this.getClass());
    
    private ILogEntryRepository repository;

    @Override
    protected void processNext(Exchange exchange) throws Exception
    {
        logger.info("Checking for exceptions");
    }

    /**
     * @return the repository
     */
    public ILogEntryRepository getRepository()
    {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(ILogEntryRepository repository)
    {
        this.repository = repository;
    }
}
