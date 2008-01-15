package com.beckproduct.log4j.appender;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.AppenderSkeleton;
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

    private QueueConnection queueConnection;

    private QueueSession queueSession;

    private QueueSender queueSender;

    @Override
    public void activateOptions()
    {
        if (!delayedActivation)
        {
            internalActivateOptions();
        }
    }

    private void internalActivateOptions()
    {
        if (activated)
        {
            return;
        }
        else
        {
            activated = true;
        }

        QueueConnectionFactory queueConnectionFactory;

        try
        {
            queueConnectionFactory = new ActiveMQConnectionFactory(brokerURL);

            LogLog.debug("Creating QueueConnection.");
            queueConnection = queueConnectionFactory.createQueueConnection();

            LogLog.debug("Creating QueueSession, transactional, " + "in AUTO_ACKNOWLEDGE mode.");
            queueSession = queueConnection.createQueueSession(true, Session.SESSION_TRANSACTED);

            LogLog.debug("Creating Queue.");
            Queue queue = queueSession.createQueue("exceptionQueue");

            LogLog.debug("Creating QueueSender.");
            queueSender = queueSession.createSender(queue);

            LogLog.debug("Starting QueueConnection.");
            queueConnection.start();
        }
        catch (Exception e)
        {
            LogLog.error("Error while activating options for appender named [" + name + "].", e);
        }
    }

    private boolean checkEntryConditions(LoggingEvent event)
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
        else if (this.queueSession == null)
        {
            fail = "No QueueSession";
        }
        else if (this.queueSender == null)
        {
            fail = "No QueueSender";
        }

        if (fail != null)
        {
            LogLog.error(fail + " for JMSAppender named [" + name + "].");
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
        if (!checkEntryConditions(event))
        {
            return;
        }

        try
        {
            LogLog.debug("About to send message!");

            ObjectMessage msg = queueSession.createObjectMessage();
            msg.setObject(event);

            queueSender.send(msg);
            queueSession.commit();

            LogLog.debug("Message sent!");
        }
        catch (Exception e)
        {
            LogLog.error("Could not publish message in JMSAppender [" + name + "].", e);
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

        LogLog.debug("Closing appender [" + name + "].");
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
}
