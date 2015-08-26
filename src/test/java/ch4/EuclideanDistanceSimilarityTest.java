package ch4;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.similarity.precompute.example.GroupLensDataModel;
import org.junit.Test;

/**
 * Created by lks21c on 15. 8. 26.
 */
public class EuclideanDistanceSimilarityTest {

	@Test
	public void EuclideanDistanceSimilarity() throws TasteException, IOException {
		DataModel model = new GroupLensDataModel(new File("ratings.dat"));
		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
			@Override
			public Recommender buildRecommender(DataModel model) throws TasteException {
				UserSimilarity similarity = new EuclideanDistanceSimilarity(model);
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(500, similarity, model);
				return new GenericUserBasedRecommender(model, neighborhood, similarity);
			}
		};
		double score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 0.05);
		System.out.println(score); // 0.68
	}
}
