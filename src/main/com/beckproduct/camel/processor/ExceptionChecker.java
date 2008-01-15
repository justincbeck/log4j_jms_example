package com.beckproduct.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.processor.DelegateProcessor;
import org.apache.log4j.Logger;

import com.beckproduct.repository.ILogEntryRepository;
import com.beckproduct.service.IEmailService;

/**
 * This class checks to see if exceptions are
 * present in the database.  If exceptions are
 * present, it sends out email notification
 * indicating how many non notified (new)
 * exceptions there are and how many non reviewed
 * exceptions there are (not new, but not reviewed).
 * 
 * @author jbeck
 */
public class ExceptionChecker extends DelegateProcessor
{
    private Logger logger = Logger.getLogger(this.getClass());

    private ILogEntryRepository repository;

    private IEmailService service;

    @Override
    protected void processNext(Exchange exchange) throws Exception
    {
        int nonNotified = repository.getNonNotifiedCount();

        int nonReviewed = repository.getNonReviewedCount();

        if (nonNotified > 0)
        {
            logger.info("Sending notification of new Exceptions.");
            service.sendNotification(nonNotified, nonReviewed);
        }
    }

    /**
     * @return the repository
     */
    public ILogEntryRepository getRepository()
    {
        return repository;
    }

    /**
     * @param repository
     *            the repository to set
     */
    public void setRepository(ILogEntryRepository repository)
    {
        this.repository = repository;
    }

    /**
     * @return the service
     */
    public IEmailService getService()
    {
        return service;
    }

    /**
     * @param service
     *            the service to set
     */
    public void setService(IEmailService service)
    {
        this.service = service;
    }
}
