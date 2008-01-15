package com.beckproduct.jms.listener;

import java.net.InetAddress;
import java.util.Date;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.beckproduct.domain.LogEntry;
import com.beckproduct.repository.ILogEntryRepository;

/**
 * This is the listener that takes the exceptions off the logging events off the
 * queue and processes them. For every message taken off the queue, a LogEntry
 * object is created and persisted to the database.
 * 
 * @author jbeck
 */
public class ExceptionListenerImpl implements MessageListener
{
    private Logger logger = Logger.getLogger(this.getClass());

    private ILogEntryRepository repository;

    /**
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void onMessage(Message jmsMessage)
    {
        try
        {
            /** Convert the Message to it's correct type */
            ObjectMessage objectMessage = (ObjectMessage) jmsMessage;
            /** Extract the LoggingEvent out of the ObjectMessage */
            LoggingEvent loggingEvent = (LoggingEvent) objectMessage.getObject();

            /**
             * Create the LogEntry that will be persisted and set all it's
             * attributes
             */
            LogEntry entry = new LogEntry();
            entry.setLogLevel(loggingEvent.getLevel().toString());
            entry.setHostName(InetAddress.getLocalHost().getHostName());
            if (loggingEvent.getThrowableStrRep() != null)
                entry.setStacktrace(StringUtils.join(loggingEvent.getThrowableStrRep(), "\n"));
            entry.setDate(new Date(loggingEvent.timeStamp));

            /** Add the new LogEntry to the database */
            repository.create(entry);
        }
        catch (Exception e)
        {
            logger.info("Boom!", e);
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
}
