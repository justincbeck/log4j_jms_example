package com.beckproduct.repository;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;

import com.beckproduct.domain.LogEntry;

/**
 * This class is used to access the database.  It is
 * used to persist new LogEntry objects and get
 * statistics on how many new entries there are.
 * 
 * @author jbeck
 */
public class LogEntryRepository extends HibernateDaoSupport implements ILogEntryRepository
{
    private PlatformTransactionManager transactionManager;

    /**
     * @see com.beckproduct.repository.ILogEntryRepository#create(com.beckproduct.domain.LogEntry)
     */
    public void create(LogEntry entry)
    {
        getHibernateTemplate().save(entry);
        getHibernateTemplate().flush();
    }

    /**
     * @see com.beckproduct.repository.ILogEntryRepository#delete(com.beckproduct.domain.LogEntry)
     */
    public void delete(LogEntry entry)
    {
        getHibernateTemplate().delete(entry);
        getHibernateTemplate().flush();
    }

    /**
     * @see com.beckproduct.repository.ILogEntryRepository#delete(java.lang.String)
     */
    public void delete(String id)
    {
        LogEntry entry = this.read(id);
        this.delete(entry);
    }

    /**
     * @see com.beckproduct.repository.ILogEntryRepository#read(java.lang.String)
     */
    public LogEntry read(String id)
    {
        return (LogEntry) getHibernateTemplate().get(LogEntry.class, id);
    }

    /**
     * @see com.beckproduct.repository.ILogEntryRepository#getNonNotifiedCount()
     */
    public int getNonNotifiedCount()
    {
        HibernateTemplate template = getHibernateTemplate();
        Session session = SessionFactoryUtils.getSession(template.getSessionFactory(), true);
        
        int count = ((Number) session.createQuery("select count(*) from LogEntry le where le.notified = '0'").uniqueResult()).intValue();
        session.createSQLQuery("update LogEntry set notified = '1'").executeUpdate();

        return count;
    }
    
    /**
     * @see com.beckproduct.repository.ILogEntryRepository#getNonReviewedCount()
     */
    public int getNonReviewedCount()
    {
        HibernateTemplate template = getHibernateTemplate();
        Session session = SessionFactoryUtils.getSession(template.getSessionFactory(), true);
        
        Query query = session.createQuery("select count(*) from LogEntry le where le.reviewed = '0'");
        return ((Number) query.uniqueResult()).intValue();
    }

    /**
     * @see com.beckproduct.repository.ILogEntryRepository#update(com.beckproduct.domain.LogEntry)
     */
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
     * @param transactionManager
     *            the transactionManager to set
     */
    public void setTransactionManager(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }
}
