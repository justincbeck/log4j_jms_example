package com.beckproduct.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Entity;
import org.hibernate.annotations.Table;

/**
 * Domain object used for persisting Logger.error
 * and Logger.warn messages to a database.
 * 
 * @author jbeck
 */

@Entity
@javax.persistence.Entity
@Table(appliesTo = "logEntry")
public class LogEntry
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    @Column(name = "logLevel", nullable = false, updatable = false, insertable = true, columnDefinition = "VARCHAR(10)")
    private String logLevel;
    
    @Column(name = "hostName", nullable = false, updatable = false, insertable = true, columnDefinition = "VARCHAR(50)")
    private String hostName;
    
    @Column(name = "message", nullable = false, updatable = false, insertable = true, columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "stacktrace", nullable = true, updatable = false, insertable = true, columnDefinition = "TEXT")
    private String stacktrace;
    
    @Column(name = "date", nullable = false, updatable = false, insertable = true, columnDefinition = "TIMESTAMP")
    private Date date;
    
    @Column(name = "reviewed", nullable = false, updatable = true, insertable = true, columnDefinition = "TINYINT(1)")
    private boolean reviewed = false;
    
    @Column(name = "notified", nullable = false, updatable = true, insertable = true, columnDefinition = "TINYINT(1)")
    private boolean notified = false;

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id)
    {
        this.id = id;
    }

    /**
     * @return the logLevel
     */
    public String getLogLevel()
    {
        return logLevel;
    }

    /**
     * @param logLevel the logLevel to set
     */
    public void setLogLevel(String logLevel)
    {
        this.logLevel = logLevel;
    }

    /**
     * @return the hostName
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * @param hostName the hostName to set
     */
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }

    /**
     * @return the stacktrace
     */
    public String getStacktrace()
    {
        return stacktrace;
    }

    /**
     * @param stacktrace the stacktrace to set
     */
    public void setStacktrace(String stacktrace)
    {
        this.stacktrace = stacktrace;
    }

    /**
     * @return the date
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date)
    {
        this.date = date;
    }

    /**
     * @return the reviewed
     */
    public boolean isReviewed()
    {
        return reviewed;
    }

    /**
     * @param reviewed the reviewed to set
     */
    public void setReviewed(boolean reviewed)
    {
        this.reviewed = reviewed;
    }

    /**
     * @return the notified
     */
    public boolean isNotified()
    {
        return notified;
    }

    /**
     * @param notified the notified to set
     */
    public void setNotified(boolean notified)
    {
        this.notified = notified;
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message)
    {
        this.message = message;
    }
}
