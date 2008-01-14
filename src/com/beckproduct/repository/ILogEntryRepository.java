package com.beckproduct.repository;

import com.beckproduct.domain.LogEntry;

public interface ILogEntryRepository
{
    public void create(LogEntry entry);
    
    public LogEntry read(String id);
    
    public void update(LogEntry entry);
    
    public void delete(LogEntry entry);
    
    public void delete(String id);
}
