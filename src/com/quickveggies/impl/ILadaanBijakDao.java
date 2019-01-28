package com.quickveggies.impl;

import java.util.List;

import com.quickveggies.entities.LadaanBijakSaleDeal;

public interface ILadaanBijakDao {

	LadaanBijakSaleDeal getLadBijSaleDeal(Long dealId);

	List<LadaanBijakSaleDeal> getLadBijSaleDealsForBuyer(String buyerTitle);

	Long getNonEditedLadaanEntries();

	

}