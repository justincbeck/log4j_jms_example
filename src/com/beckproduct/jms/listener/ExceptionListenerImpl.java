package com.beckproduct.jms.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class ExceptionListenerImpl implements MessageListener
{
    private Logger logger = Logger.getLogger(this.getClass());

    public void onMessage(Message message)
    {
        try
        {
            ObjectMessage m = (ObjectMessage) message;
            LoggingEvent e = (LoggingEvent) m.getObject();
            
            logger.info(e.getRenderedMessage());
        }
        catch (Exception e)
        {
            logger.info("Boom!", e);
        }
    }
}
