package ch3;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.junit.Test;

/**
 * Created by lks21c on 15. 8. 25.
 */
public class RecommanderTest {

	@Test
	public void refreshRecommander() throws TasteException, IOException {
		DataModel model = new FileDataModel(new File("u.data"));
		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);

		System.out.println("user 1 and 2 similarity : " + similarity.userSimilarity(1, 2));
		System.out.println("user 1 and 3 similarity : " + similarity.userSimilarity(1, 3));
		System.out.println("user 1 and 4 similarity : " + similarity.userSimilarity(1, 4));

		UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);

		System.out
				.println("user 1's naver : " + ToStringBuilder.reflectionToString(neighborhood.getUserNeighborhood(1)));

		UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
		recommender.refresh(null);
	}
}
