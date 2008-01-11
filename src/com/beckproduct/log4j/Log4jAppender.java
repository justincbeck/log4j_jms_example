package com.beckproduct.log4j;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.management.ServiceNotFoundException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class Log4jAppender extends AppenderSkeleton
{
    protected void append(LoggingEvent event)
    {
        try
        {
            JMSServiceFactory jmsService = JMSServiceFactory.getJmsServiceFactory();
            QueueConnectionFactory queueFactory = (QueueConnectionFactory) jmsService.getTargetJMSConnectionFactory("jms/LogConnectionFactory", serviceLocator);
            QueueConnection queueConnection = queueFactory.createQueueConnection();
            QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) jmsService.getTargetJMSQueue("jms/LogQueue", serviceLocator);
            QueueSender queueSender = queueSession.createSender(queue);
            TextMessage msg = queueSession.createTextMessage();
            msg.setText(getLayout().format(event));
            queueSender.send(msg);
        }
        catch (ServiceNotFoundException snfe)
        {
            snfe.printStackTrace(System.out);
        }
        catch (JMSException jmse)
        {
            jmse.printStackTrace(System.out);
        }
    }

    public Object lookupService(String serviceName) throws ServiceNotFoundException
    {
        try
        {
            InitialContext initialContext = new InitialContext();
            java.lang.Object obj = initialContext.lookup(serviceName);
            return obj;
        }
        catch (NamingException ne)
        {
            ne.printStackTrace(System.out);
            throw new ServiceNotFoundException(ne.getExplanation());
        }
    }

    public void close()
    {

    }

    public boolean requiresLayout()
    {
        return true;
    }
}
