package com.beckproduct.jms.listener;

import java.net.InetAddress;
import java.util.Date;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;

import com.beckproduct.domain.LogEntry;

public class ExceptionListenerImpl extends HibernateDaoSupport implements MessageListener
{
    private Logger logger = Logger.getLogger(this.getClass());
    
    private PlatformTransactionManager transactionManager;
    
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
            
            this.create(entry);
        }
        catch (Exception e)
        {
            logger.info("Boom!", e);
        }
    }
    
    private void create(LogEntry entry)
    {
        getHibernateTemplate().saveOrUpdate(entry);
        getHibernateTemplate().flush();
    }

    /**
     * @return the transactionManager
     */
    public PlatformTransactionManager getTransactionManager()
    {
        return transactionManager;
    }

    /**
     * @param transactionManager the transactionManager to set
     */
    public void setTransactionManager(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }
}
