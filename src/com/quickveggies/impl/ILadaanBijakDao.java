package com.quickveggies.impl;

import java.util.List;

import com.quickveggies.entities.LadaanBijakSaleDeal;

public interface ILadaanBijakDao {

	LadaanBijakSaleDeal getLadBijSaleDeal(int dealId);

	List<LadaanBijakSaleDeal> getLadBijSaleDealsForBuyer(String buyerTitle);

	int getNonEditedLadaanEntries();

}