package ch3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.junit.Test;

import javax.sql.DataSource;

/**
 * Created by lks21c on 15. 8. 25.
 */
public class DataModelTest {

	@Test
	public void genericModel() {

		GenericUserPreferenceArray preferenceArr = new GenericUserPreferenceArray(2);

		preferenceArr.setUserID(0, 1);

		preferenceArr.setItemID(0, 1);
		preferenceArr.setValue(0, 1.0f);

		preferenceArr.setItemID(1, 2);
		preferenceArr.setValue(1, 2.0f);

		Preference pref = preferenceArr.get(0);

		assertEquals(1, pref.getUserID());
		assertEquals(1, pref.getItemID());
		assertTrue(pref.getValue() == 1.0f);

		FastByIDMap<PreferenceArray> preferences = new FastByIDMap<PreferenceArray>();
		preferences.put(0L, preferenceArr);
		DataModel model = new GenericDataModel(preferences);

		// refresh model
		model.refresh(null);
	}

	@Test
	public void jdbcDataModel() throws TasteException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://hydra01.ipdisk.co.kr:6033/torrent_vod?autoReconnect=true");
        dataSource.setUsername("root");
        // assume target table as taste_preferences table by default.
        JDBCDataModel jdbcDataModel = new MySQLJDBCDataModel(dataSource);
	}
}
