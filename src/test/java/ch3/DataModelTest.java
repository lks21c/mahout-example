package ch3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.*;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.junit.Test;

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

	@Test
	public void notSuccessfulGenericBooleanPrefDataModel() throws IOException, TasteException {
		DataModel model = new GenericBooleanPrefDataModel(
				GenericBooleanPrefDataModel.toDataMap(new FileDataModel(new File("ua.base"))));

		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel model) throws TasteException {
				UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
				return new GenericUserBasedRecommender(model, neighborhood, similarity);
			}
		};
		DataModelBuilder modelBuilder = new DataModelBuilder() {
			public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {
				return new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(trainingData));
			}
		};

		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		try {
			double score = evaluator.evaluate(recommenderBuilder, modelBuilder, model, 0.9, 1.0);
			System.out.println(score);
			assertTrue(false);
		} catch (IllegalArgumentException e) {
			// This is natural since DataModel doesn't have preference values.
			// PearsonCorrelationSimilarity needs preference values.
			e.printStackTrace();
			assertTrue(true);
		}

	}

	@Test
	public void successfulGenericBooleanPrefDataModel() throws IOException, TasteException {
		DataModel model = new GenericBooleanPrefDataModel(
				GenericBooleanPrefDataModel.toDataMap(new FileDataModel(new File("ua.base"))));

		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel model) throws TasteException {
				UserSimilarity similarity = new LogLikelihoodSimilarity(model);
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
				return new GenericUserBasedRecommender(model, neighborhood, similarity);
			}
		};

		DataModelBuilder modelBuilder = new DataModelBuilder() {
			public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {
				return new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(trainingData));
			}
		};

		RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();
		IRStatistics stats = evaluator.evaluate(recommenderBuilder, modelBuilder, model, null, 10,
				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		System.out.println(stats.getPrecision());
		System.out.println(stats.getRecall());
	}
}
