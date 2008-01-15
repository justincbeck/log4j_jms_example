package com.beckproduct.service;

/**
 * An interface for the email service
 * 
 * @author jbeck
 */
public interface IEmailService
{
    /**
     * Sends an email notification.
     * <p>
     * 
     * @param nonNotified
     *            The number of new exceptions
     * @param nonReviewed
     *            The number of exceptions to be reviewed
     */
    public void sendNotification(int nonNotified, int nonReviewed);
}
