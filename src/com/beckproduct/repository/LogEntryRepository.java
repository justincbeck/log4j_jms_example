package com.beckproduct.repository;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
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

    public int getNonNotifiedCount()
    {
        HibernateTemplate template = getHibernateTemplate();
        Session session = SessionFactoryUtils.getSession(template.getSessionFactory(), true);
        
        int count = ((Number) session.createQuery("select count(*) from LogEntry le where le.notified = '0'").uniqueResult()).intValue();
        session.createSQLQuery("update LogEntry set notified = '1'").executeUpdate();

        return count;
    }
    
    public int getNonReviewedCount()
    {
        HibernateTemplate template = getHibernateTemplate();
        Session session = SessionFactoryUtils.getSession(template.getSessionFactory(), true);
        
        Query query = session.createQuery("select count(*) from LogEntry le where le.reviewed = '0'");
        return ((Number) query.uniqueResult()).intValue();
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
     * @param transactionManager
     *            the transactionManager to set
     */
    public void setTransactionManager(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }
}
