package com.beckproduct.repository;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;

import com.beckproduct.domain.LogEntry;

public class LogEntryRepository extends HibernateDaoSupport implements ILogEntryRepository
{
    private PlatformTransactionManager transactionManager;
    
    public void create(LogEntry entry)
    {
        getHibernateTemplate().save(entry);
        getHibernateTemplate().flush();
    }

    public void delete(LogEntry entry)
    {
        getHibernateTemplate().delete(entry);
        getHibernateTemplate().flush();
    }

    public void delete(String id)
    {
        LogEntry entry = this.read(id);
        this.delete(entry);
    }

    public LogEntry read(String id)
    {
        return (LogEntry) getHibernateTemplate().get(LogEntry.class, id);
    }

    public void update(LogEntry entry)
    {
        getHibernateTemplate().update(entry);
        getHibernateTemplate().flush();
    }

    /**
     * @return the transactionManager
     */
    public PlatformTransactionManager getTransactionManager()
    {
        return transactionManager;
    }

    /**
     * @param transactionManager the transactionManager to set
     */
    public void setTransactionManager(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }
}
