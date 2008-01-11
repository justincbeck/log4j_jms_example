package com.beckproduct.jms;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class ExceptionListenerImpl implements MessageListener
{
    private Logger logger = Logger.getLogger(this.getClass());
    
    /**
     * The dataSource for this class.
     */
    private DataSource dataSource;

    public void onMessage(Message message)
    {
        logger.info("Exception logged!");
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
