package com.beckproduct.log4j.appender;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

public class JMSAppender extends AppenderSkeleton
{
    boolean delayedActivation;

    boolean activated;
    
    String brokerURL;

    QueueConnection queueConnection;

    QueueSession queueSession;

    QueueSender queueSender;

    public JMSAppender()
    {
    }

    public void setDelayedActivation(boolean delayedActivation)
    {
        this.delayedActivation = delayedActivation;
    }

    public boolean getDelayedActivation()
    {
        return delayedActivation;
    }
    
    public void setBrokerURL(String brokerURL)
    {
        this.brokerURL = brokerURL;
    }
    
    public String getBrokerURL()
    {
        return brokerURL;
    }

    public void activateOptions()
    {
        if (!delayedActivation)
        {
            internalActivateOptions();
        }
    }

    protected void internalActivateOptions()
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

            LogLog.debug("About to create QueueConnection.");
            queueConnection = queueConnectionFactory.createQueueConnection();

            LogLog.debug("Creating QueueSession, transactional, " + "in AUTO_ACKNOWLEDGE mode.");
            queueSession = queueConnection.createQueueSession(true, Session.SESSION_TRANSACTED);

            LogLog.debug("About to create Queue.");
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

    protected Object lookup(Context ctx, String name) throws NamingException
    {
        try
        {
            return ctx.lookup(name);
        }
        catch (NameNotFoundException e)
        {
            LogLog.error("Could not find name [" + name + "].", e);
            throw e;
        }
    }

    protected boolean checkEntryConditions(LoggingEvent event)
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

    public synchronized void close()
    {
        if (this.closed)
            return;

        LogLog.debug("Closing appender [" + name + "].");
        this.closed = true;
    }

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

    public boolean requiresLayout()
    {
        return false;
    }
}
