package com.beckproduct.jms;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class ExceptionListenerImpl implements MessageListener
{
    private Logger logger = Logger.getLogger(this.getClass());

    /**
     * The dataSource for this class.
     */
    private DataSource dataSource;

    public void onMessage(Message message)
    {
        try
        {
            ObjectMessage m = (ObjectMessage) message;
            LoggingEvent e = (LoggingEvent) m.getObject();
            
            logger.info("Kaboom!", e.getThrowableInformation().getThrowable());
        }
        catch (Exception e)
        {
            logger.info("Boom!", e);
        }
    }

    /**
     * @param dataSource
     *            the dataSource to set
     */
    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
}
