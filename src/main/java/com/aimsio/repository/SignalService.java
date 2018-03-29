package com.aimsio.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;

/**
 * This service provides database operations related Signal entity.
 * 
 * @author Samir
 *
 */
@Component
public class SignalService {

	private static final String RETRIEVE_STATUS_BY_ASSETUN = "SELECT distinct status FROM `signal` where AssetUN = ? Order by status;";

	private static final String RETRIEVE_ALL_ASSETUNS = "SELECT distinct AssetUN FROM `signal` ORDER BY `signal`.AssetUN;";

	private static final String RETRIEVE_SIGNALS_BY_STATUS_ASSETUN = "SELECT count(status) cnt, `signal`.entry_date entryDate FROM `signal` WHERE `signal`.`AssetUN` = ? AND `signal`.status = ? GROUP BY entry_date ORDER BY entry_date;";
	
	private static final String RETRIEVE_SIGNALS_BY_ASSETUN = "SELECT count(status) cnt, `signal`.entry_date entryDate, status FROM `signal` WHERE `signal`.`AssetUN` = ? GROUP BY entry_date, status ORDER BY entry_date;";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<String> retrieveAllAssetUNs() {
		return jdbcTemplate.query(RETRIEVE_ALL_ASSETUNS, (rs, rowNum) -> rs.getString("AssetUN"));
	}

	public Map<String, Number> getSignalsForGivenAssetUNAndStatus(String status, String assetUN) {
		List<Map<String, Object>> signalsForGivenAssetUNAndStatus = jdbcTemplate.queryForList(RETRIEVE_SIGNALS_BY_STATUS_ASSETUN, assetUN, status);
		Map<String, Number> signalCountMap = new TreeMap<>();
		for (Map<String, Object> map : signalsForGivenAssetUNAndStatus) {
			signalCountMap.put(((java.sql.Date) map.get("entryDate")).toString(), (Long) map.get("cnt"));
		}
		return signalCountMap;
	}
	
	public Map<String, Map<String, Number>> getSignalsForGivenAssetUN (String assetUN) {
		List<Map<String, Object>> signalsForGivenAssetUNAndStatus = jdbcTemplate.queryForList(RETRIEVE_SIGNALS_BY_ASSETUN, assetUN);
		Map<String, Map<String, Number>> signalStatusMap = new HashMap<>();
		for (Map<String, Object> map : signalsForGivenAssetUNAndStatus) {
			if(signalStatusMap.get(map.get("status")) == null) {
				signalStatusMap.put((String) map.get("status"), new TreeMap<>());
			}
			signalStatusMap.get(map.get("status")).put(((java.sql.Date) map.get("entryDate")).toString(), (Long) map.get("cnt"));
		}
		return signalStatusMap;
	}

	public Collection<String> retrieveStatusByAssetUN(String assetUN) {
		return jdbcTemplate.query(RETRIEVE_STATUS_BY_ASSETUN, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, assetUN);

			}
		}, (rs, rowNum) -> rs.getString("status"));
	}

}
