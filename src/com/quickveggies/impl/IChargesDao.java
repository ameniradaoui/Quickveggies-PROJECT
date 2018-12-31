package com.quickveggies.impl;

import java.util.List;
import java.util.Map;

import com.quickveggies.controller.ChargeTypeValueMap;
import com.quickveggies.entities.Charge;

public interface IChargesDao {

	void addDealCharges(Map<String, ChargeTypeValueMap> map, int dealID);

	List<Charge> getDealCharges(int dealID);

}