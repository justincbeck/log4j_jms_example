package com.beckproduct.log4j.appender;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This is a new Appender used with Log4J. The JMSAppender that comes with Log4J
 * only allows the ability to append messages to a Topic rather than a Queue.
 * This Appender allows for messages to be appended to a Queue (but not a
 * topic).
 * 
 * @author jbeck
 */
public class JMSAppender extends AppenderSkeleton
{
    private boolean delayedActivation;

    private boolean activated;

    private String brokerURL;
    
    private String queueName;

    private QueueConnection queueConnection;
    
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    public void activateOptions()
    {
        if (!delayedActivation)
        {
            this.internalActivateOptions();
        }
    }
    
    private void internalActivateOptions()
    {
        if (activated)
        {
            return;
        }
        QueueConnectionFactory queueConnectionFactory;

        try
        {
            queueConnectionFactory = new ActiveMQConnectionFactory(brokerURL);

            logger.info("Creating QueueConnection.");
            queueConnection = (ActiveMQConnection) queueConnectionFactory.createConnection();

            activated = true;
        }
        catch (JMSException e)
        {
            LogLog.error("Error while activating options for appender named [" + name + "].", e);
        }
    }

    private boolean checkEntryConditions()
    {
        String fail = null;

        if (delayedActivation)
        {
            internalActivateOptions();
        }

        if (this.queueConnection == null)
        {
            fail = "No QueueConnection";
        }

        if (fail != null)
        {
            logger.info(fail + " for JMSAppender named [" + name + "].");
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
     */
    public void append(LoggingEvent event)
    {
        if (!checkEntryConditions())
        {
            return;
        }
        
        try
        {
            logger.info("About to send message!");
            
            logger.info("Creating QueueSession, non-transactional, " + "in AUTO_ACKNOWLEDGE mode.");
            QueueSession queueSession = (QueueSession) queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            logger.info("Creating Destination.");
            Destination destination = queueSession.createQueue(queueName);

            logger.info("Creating Producer.");
            MessageProducer producer = queueSession.createProducer(destination);
            
            logger.info("Sending Message.");
            producer.send(queueSession.createObjectMessage(event));
            
            logger.info("Closing Session.");
            queueSession.close();

            logger.info("Message sent!");
        }
        catch (Exception e)
        {
            logger.info("Could not publish message in JMSAppender [" + name + "].", e);
        }
    }

    /**
     * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
     */
    public boolean requiresLayout()
    {
        return false;
    }

    /**
     * @see org.apache.log4j.AppenderSkeleton#close()
     */
    public synchronized void close()
    {
        if (this.closed)
            return;

        logger.info("Closing appender [" + name + "].");
        this.closed = true;
    }

    /**
     * @return the delayedActivation
     */
    public boolean isDelayedActivation()
    {
        return delayedActivation;
    }

    /**
     * @param delayedActivation
     *            the delayedActivation to set
     */
    public void setDelayedActivation(boolean delayedActivation)
    {
        this.delayedActivation = delayedActivation;
    }

    /**
     * @return the brokerURL
     */
    public String getBrokerURL()
    {
        return brokerURL;
    }

    /**
     * @param brokerURL
     *            the brokerURL to set
     */
    public void setBrokerURL(String brokerURL)
    {
        this.brokerURL = brokerURL;
    }

    /**
     * @return the queueName
     */
    public String getQueueName()
    {
        return queueName;
    }

    /**
     * @param queueName the queueName to set
     */
    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }
}