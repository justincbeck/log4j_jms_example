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

public class ExceptionListenerImpl implements MessageListener
{
    private Logger logger = Logger.getLogger(this.getClass());
    
    private ILogEntryRepository repository;
    
    public void onMessage(Message jmsMessage)
    {
        try
        {
            ObjectMessage objectMessage = (ObjectMessage) jmsMessage;
            LoggingEvent loggingEvent = (LoggingEvent) objectMessage.getObject();
            
            LogEntry entry = new LogEntry();
            entry.setLogLevel(loggingEvent.getLevel().toString());
            entry.setHostName(InetAddress.getLocalHost().getHostName());
            entry.setStacktrace(StringUtils.join(loggingEvent.getThrowableStrRep(), "\n"));
            entry.setDate(new Date(loggingEvent.timeStamp));
            
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
     * @param repository the repository to set
     */
    public void setRepository(ILogEntryRepository repository)
    {
        this.repository = repository;
    }
}
