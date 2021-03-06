package com.quickveggies.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import com.quickveggies.GeneralMethods;
import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.BoxSize;
import com.quickveggies.entities.QualityType;
import com.quickveggies.entities.User;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IBoxSizesDao;

import com.quickveggies.impl.IUserUtils;

@Component
public class BoxSizesDao implements IBoxSizesDao {

	@Autowired
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	private JdbcTemplate template;

	private void initTemplate() {
		if (template == null) {
			template = new JdbcTemplate(dataSource);

		}
	}

	private SimpleJdbcInsert insert;

	private void initInsert() {
		if (insert == null) {
			createInsert();

		}
	}

	private void createInsert() {
		insert = new SimpleJdbcInsert(dataSource).withTableName("users").usingGeneratedKeyColumns("id");
	}

	public void setDataSource(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
		createInsert();

	}

	private static RowMapper<BoxSize> mapper = new RowMapper<BoxSize>() {
		@Override
		public BoxSize mapRow(ResultSet data, int index) throws SQLException {
			BoxSize item = new BoxSize();
			item.setId(data.getLong("id"));
			item.setName(data.getString("name"));
			

			return item;
		}
	};

	// private static final String INSERT_QUALITY_QRY = "IF NOT EXISTS (SELECT *
	// FROM qualities WHERE name = ? COLLATE SQL_Latin1_General_CP1_CI_AS)
	// INSERT INTO qualities (name) VALUES (?)";
	private static final String INSERT_QUALITY_QRY = "select case when (count(name)=0) then (select qualities_enter(?)) end  from qualities where name =?";

	// private static final String INSERT_BOX_SIZE_QRY = "IF NOT TRUE (SELECT *
	// FROM boxSizes WHERE name = ?) INSERT INTO boxSizes (name) VALUES (?)";
	private static final String INSERT_BOX_SIZE_QRY = "select case when (count(name) = 0) then (select boxsize_entry(?)) end from boxsizes where name = ?";

	// private static final String INSERT_FRUIT_BOX_QRY = "IF NOT EXISTS (SELECT
	// * FROM fruitBoxSizes WHERE fruit_id = ? AND boxSize_id = ?) Insert into
	// fruitBoxSizes (fruit_id, boxSize_id) values (?,?)";
	private static final String INSERT_FRUIT_BOX_QRY = "select case when  (count(boxsize_id) = 0) then (select fruitboxsize_entry(?,?)) end from fruitboxsizes where fruit_id = ? AND boxSize_id = ?";

	// private static final String INSERT_FRUIT_QUALITY_QRY = "IF NOT EXISTS
	// (SELECT * FROM fruitQuality WHERE fruit_id = ? AND quality_id = ?) Insert
	// into fruitQuality (fruit_id,quality_id) values (?,?)";
	private static final String INSERT_FRUIT_QUALITY_QRY = "select case when (count(*)=0) then (fruitquality(?,?)) end from fruitquality where fruit_id = ? and quality_id = ?";
	// private static final String INSERT_FRUIT_QRY = "IF NOT EXISTS (SELECT *
	// FROM fruits WHERE name = ? COLLATE SQL_Latin1_General_CP1_CI_AS) INSERT
	// INTO fruits (name) VALUES (?)";
	private static final String INSERT_FRUIT_QRY = "select case when (count(name)=0) then (select fruit_entry(?)) end from fruits where name=?";

	private static final String SELECT_FRUIT_QUALITY_QRY = "select * from qualities qt where qt.id in (select quality_id from fruitQuality where fruit_id in(select id from fruits where name=?) )";

	@Autowired
	private AuditDao auditDao;
	@Autowired
	private UserUtils userDao;
	
	
	
	@Override
	public List<String> getAllFruitTypes() {
		initTemplate();
		List<String> fNames = new ArrayList<>();
		String sql = "Select name from fruits";
		
		fNames = template.query(sql, SingleColumnRowMapper.newInstance(String.class));
		if (fNames.isEmpty()){
	    	   return null ;
	       } else {
	    	   return fNames;
	       }
		
//		try {
//			ResultSet rs = getResult(sql);
//			while (rs.next()) {
//				fNames.add(rs.getString(1));
//			}
//			rs.close();
//
//		} catch (Exception ex) {
//			GeneralMethods.errorMsg("Error getting fruit details from DB:" + ex.getMessage());
//		}
//
	//	return fNames;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBoxSizesDao#addFruit(java.lang.String)
	 */
	@Override
	public int addFruit(String fruit) {
		int id = -1;
		Boolean autoCommit = null;
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement(
					"select case when (count(name)=0) then (select fruit_entry(?)) end from fruits where name=?");
			autoCommit = dataSource.getConnection().getAutoCommit();
			dataSource.getConnection().setAutoCommit(true);
			if (fruit != null) {

				ps.setString(1, fruit);
				ps.setString(2, fruit);
				// ps.executeUpdate();
				ps.execute();
				auditDao.insertAuditRecord(new AuditLog(userDao.getCurrentUser(), new Date(),
						"ADDED Entry for fruit:".concat(fruit), null, 0l));

				ps = connection.prepareStatement("Select id from fruits where name=?");
				ps.setString(1, fruit);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					id = rs.getInt(1);
				}
				ps.close();
				connection.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (autoCommit != null) {
				try {
					dataSource.getConnection().setAutoCommit(autoCommit);
				} catch (SQLException ignored) {
				}
			}
		}
		return id;
	}

	/**
	 * Adds the qualities to table
	 *
	 * @param qualities
	 * @author Shoeb
	 */
	// ## chnaged by ss
	private void addFruitQualities(List<String> qualities) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(INSERT_QUALITY_QRY)) {
			for (String quality : qualities) {
				if (quality == null || quality.trim().isEmpty()) {
					continue;
				}
				ps.setString(1, quality);
				ps.setString(2, quality);
				ps.addBatch();
			}

			// ps.executeBatch();
			ps.execute();
			ps.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ResultSet getResult(String query) throws SQLException {
		Statement statement = dataSource.getConnection().createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		return resultSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBoxSizesDao#getAllFruitTypes()
	 */
	

	// ## changed by ss
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IBoxSizesDao#addFruitQualities(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public void addFruitQualities(String fruitName, List<String> qualityStrings) {
		int fruitId = addFruit(fruitName);
		Map<String, QualityType> existingQualitiesMap = getDetailForQuality(qualityStrings);
		List<String> newQualityToInsertList = new ArrayList<>();
		for (String qualName : qualityStrings) {
			if (!existingQualitiesMap.containsKey(qualName)) {
				newQualityToInsertList.add(qualName);
			}
		}
		addFruitQualities(newQualityToInsertList);
		List<QualityType> combinedQualityList = new ArrayList<>();
		if (!newQualityToInsertList.isEmpty()) {
			Map<String, QualityType> newlyInsertedQuality = getDetailForQuality(newQualityToInsertList);
			combinedQualityList.addAll(newlyInsertedQuality.values());
		}

		combinedQualityList.addAll(existingQualitiesMap.values());
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(INSERT_FRUIT_QUALITY_QRY)) {
			StringBuilder sb = new StringBuilder();
			for (QualityType qt : combinedQualityList) {
				ps.setInt(1, fruitId);
				ps.setLong(2, qt.getId());
				ps.setInt(3, fruitId);
				ps.setLong(4, qt.getId());
				ps.addBatch();
				sb.append(qt.getName() + " , ");

			}
			// ps.executeBatch();
			ps.execute();
			auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
					"ADDED Fruit Qualities :".concat(sb.toString()), null, 0l));
			ps.close();
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBoxSizesDao#addFruitBoxSizes(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public void addFruitBoxSizes(String fruitName, List<String> boxSizeStrings) {
		int fruitId = addFruit(fruitName);
		Map<String, BoxSize> existingBoxSizeMap = getDetailForBoxSize(boxSizeStrings);
		List<String> newBoxToInsertList = new ArrayList<>();
		for (String bsName : boxSizeStrings) {
			if (!existingBoxSizeMap.containsKey(bsName)) {
				newBoxToInsertList.add(bsName);
			}
		}
		addBoxSizes(newBoxToInsertList);
		List<BoxSize> combinedBoxSizeList = new ArrayList<>();
		if (!newBoxToInsertList.isEmpty()) {
			Map<String, BoxSize> newlyInsertedBox = getDetailForBoxSize(newBoxToInsertList);
			combinedBoxSizeList.addAll(newlyInsertedBox.values());
		}
		combinedBoxSizeList.addAll(existingBoxSizeMap.values());
		try (PreparedStatement ps = dataSource.getConnection().prepareStatement(INSERT_FRUIT_BOX_QRY)) {
			for (BoxSize bs : combinedBoxSizeList) {
				ps.setInt(1, fruitId);
				ps.setLong(2, bs.getId());
				ps.setInt(3, fruitId);
				ps.setLong(4, bs.getId());
				// ps.addBatch();
				ps.execute();
			}
			ps.executeBatch();
			ps.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void addBoxSizes(List<String> boxSizes) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(INSERT_BOX_SIZE_QRY)) {
			for (String boxSize : boxSizes) {
				if (boxSize == null || boxSize.trim().isEmpty()) {
					continue;
				}
				// System.out.println(boxSize.s);
				boxSize = boxSize.toLowerCase();

				ps.setString(1, boxSize);
				ps.setString(2, boxSize);
				// ps.addBatch();
				ps.execute();
			}
			ps.executeBatch();
			ps.close();
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IBoxSizesDao#getDetailForBoxSize(java.util.List)
	 */
	@Override
	public Map<String, BoxSize> getDetailForBoxSize(List<String> inBoxSizes) {
		Map<String, BoxSize> map = new LinkedHashMap<>();
		StringBuilder selectQry = new StringBuilder("Select * from boxSizes where name in (");
		if (inBoxSizes != null) {

			for (String bs : inBoxSizes) {
				if (bs == null || bs.trim().isEmpty()) {
					continue;
				}
				selectQry.append("'").append(bs.trim().toLowerCase()).append("'").append(",");
			}
			selectQry.deleteCharAt(selectQry.lastIndexOf(","));
			selectQry.append(")");
		}
		try {
			ResultSet rs = getResult(selectQry.toString());
			while (rs.next()) {
				BoxSize box = new BoxSize();
				box.setId(rs.getLong("id"));
				box.setName(rs.getString("name"));
				map.put(box.getName(), box);
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IBoxSizesDao#getDetailForQuality(java.util.List)
	 */
	@Override
	public Map<String, QualityType> getDetailForQuality(List<String> inQualityList) {
		Map<String, QualityType> map = new LinkedHashMap<>();
		StringBuilder selectQry = new StringBuilder("Select * from qualities where name in (");
		if (inQualityList == null || inQualityList.isEmpty()) {
			throw new IllegalArgumentException("Empty quality  list passed");
		}
		for (String qual : inQualityList) {
			if (qual == null || qual.trim().isEmpty()) {
				continue;
			}
			selectQry.append("'").append(qual.trim().toLowerCase()).append("'").append(",");
		}
		selectQry.deleteCharAt(selectQry.lastIndexOf(","));
		selectQry.append(")");
		try {
			ResultSet rs = getResult(selectQry.toString());
			while (rs.next()) {
				QualityType quality = new QualityType();
				quality.setId(rs.getLong("id"));
				quality.setName(rs.getString("name"));
				map.put(quality.getName(), quality);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IBoxSizesDao#getBoxSizesForFruit(java.lang.String)
	 */
	@Override
	public List<BoxSize> getBoxSizesForFruit(String fruitName) {
		List<BoxSize> boxSizes = new ArrayList<>();
		String boxesForFruitQry = "select * from boxSizes bs where bs.id in (select boxSize_id from fruitBoxSizes where fruit_id in(select id from fruits where name=?) )";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(boxesForFruitQry)) {
			ps.setString(1, fruitName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				BoxSize bs = new BoxSize();
				bs.setId(rs.getLong("id"));
				bs.setName(rs.getString("name"));
				boxSizes.add(bs);

			}
			ps.close();
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return boxSizes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBoxSizesDao#getQualityTypesForFruit(java.lang.
	 * String)
	 */
	@Override
	public List<QualityType> getQualityTypesForFruit(String fruitName) {
		List<QualityType> qualityTypes = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(SELECT_FRUIT_QUALITY_QRY)) {
			ps.setString(1, fruitName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				QualityType qt = new QualityType();
				qt.setId(rs.getLong("id"));
				qt.setName(rs.getString("name"));
				qualityTypes.add(qt);

			}
			connection.close();
			ps.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return qualityTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IBoxSizesDao#deleteFruitDetails(java.lang.String)
	 */
	@Override
	public void deleteFruitDetails(String fruitName) {
		String sqlDelFruitQuals = "Delete from fruitQuality where fruit_id in(select id from fruits where name=?)";
		String sqlDelFruitBoxTypes = "Delete from fruitBoxSizes where fruit_id in(select id from fruits where name=?)";
		String[] queries = new String[] { sqlDelFruitBoxTypes, sqlDelFruitQuals };
		try {
			for (String query : queries) {
				Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);
				ps.setString(1, fruitName);
				ps.executeUpdate();
				ps.close();

				connection.close();
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBoxSizesDao#deleteFruit(java.lang.String)
	 */
	@Override
	public void deleteFruit(String fruitName) {
		String query = "Delete from  fruits where name=?";
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, fruitName);
			ps.executeUpdate();
			auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
					"DELETED fruit entry:".concat(fruitName), null, 0l));
			ps.close();
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		deleteFruitDetails(fruitName);
	}

	// ## for audit log insert
	// ## changed by ss

	int getGeneratedKey(PreparedStatement ps) throws SQLException {
		ResultSet genKeyRs = ps.getGeneratedKeys();
		if (genKeyRs != null && genKeyRs.next()) {
			return genKeyRs.getInt(1);
		}
		System.err.println(String.format("Unable to get the generated id for the object %s, setting it to 0",
				ps.getMetaData().getTableName(1)));
		return 0;

	}

}
