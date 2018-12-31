package com.quickveggies.impl;

import java.util.List;
import java.util.Map;

import com.quickveggies.entities.BoxSize;
import com.quickveggies.entities.QualityType;

public interface IBoxSizesDao {

	int addFruit(String fruit);

	List<String> getAllFruitTypes();

	//## changed by ss
	void addFruitQualities(String fruitName, List<String> qualityStrings);

	/**
	 * Use this method to create a new list of box size types for a fruit, if
	 * box size types with the same name are found in the DB, then the old box
	 * size types would be used, otherwise new entries would be created. This
	 * method also associates the specified fruit with the passed in box size
	 * types.
	 *
	 * @param fruitName
	 * @param boxSizeStrings
	 */
	void addFruitBoxSizes(String fruitName, List<String> boxSizeStrings);

	/**
	 * Returns box size object for give box size name
	 *
	 * @param inBoxSizes
	 * @return
	 */
	Map<String, BoxSize> getDetailForBoxSize(List<String> inBoxSizes);

	Map<String, QualityType> getDetailForQuality(List<String> inQualityList);

	List<BoxSize> getBoxSizesForFruit(String fruitName);

	List<QualityType> getQualityTypesForFruit(String fruitName);

	void deleteFruitDetails(String fruitName);

	void deleteFruit(String fruitName);

}