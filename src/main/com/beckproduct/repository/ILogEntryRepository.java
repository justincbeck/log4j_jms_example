package com.beckproduct.repository;

import com.beckproduct.domain.LogEntry;

/**
 * An interface for the logEntry repository
 * 
 * @author jbeck
 */
public interface ILogEntryRepository
{
    /**
     * Persists a LogEntry to the database
     * <p>
     * 
     * @param logEntry
     *            The logEntry to persist
     */
    public void create(LogEntry entry);
    
    /**
     * Reads a logEntry from the database
     * <p>
     * 
     * @param id
     *            The id of the logEntry to retrieve
     * @returns 
     *            The logEntry
     */
    public LogEntry read(String id);
    
    /**
     * Returns the number of new exceptions
     * <p>
     * 
     * @returns
     *            The number of new exceptions
     */
    public int getNonNotifiedCount();
    
    /**
     * Returns the number of non-reviewed exceptions
     * <p>
     * 
     * @returns
     *            The number of exceptions to be reviewed
     */
    public int getNonReviewedCount();
    
    /**
     * Updates a logEntry
     * <p>
     * 
     * @param entry
     *            The logEntry to be updated
     */
    public void update(LogEntry entry);
    
    /**
     * Deletes a logEntry from the database
     * <p>
     * 
     * @param logEntry
     *            The logEntry to remove
     */
    public void delete(LogEntry entry);
    
    /**
     * Deletes a logEntry from the database
     * <p>
     * 
     * @param id
     *            The id of the logEntry to remove
     */
    public void delete(String id);
}
