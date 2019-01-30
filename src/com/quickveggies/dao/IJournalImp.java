package com.quickveggies.dao;

import java.io.IOException;
import java.util.List;

import com.quickveggies.entities.Journal;

public interface IJournalImp {

	List<Journal> getJournals();

	Long insertJournal(Journal item) throws IOException;

	void updateJournalInfo(Journal item, String name);

	

	List<Journal> getJournalsTable();

	void deleteJournal(Long id);

}