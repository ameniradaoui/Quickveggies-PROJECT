package com.quickveggies.impl;

import java.util.List;
import java.util.Map;

import com.quickveggies.controller.ChargeTypeValueMap;

import com.quickveggies.entities.Charge;

public interface IChargesDao {

	

	List<Charge> getDealCharges(int dealID);

	

	void addDealCharges(Map<String, ChargeTypeValueMap> map, int dealID);

}