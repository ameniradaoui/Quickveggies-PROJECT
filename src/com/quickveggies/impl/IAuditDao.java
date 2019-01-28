package com.quickveggies.impl;

import java.util.List;

import com.quickveggies.entities.AuditLog;

public interface IAuditDao {

	List<AuditLog> getAuditRecords();

	Long insertAuditRecord(AuditLog log);

}