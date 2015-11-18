package main; /**
 * Created by Adwait on 11/10/2015.
 */

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.List;



public class SampleRecommender {
  /*  public static void main (String args[])*/
    @Autowired
    private CustomerRepository userRatingRepository;

    public List<RecommendedItem>  getRecommendations() throws IOException, TasteException
       {

           userRatingRepository.save(new Customer("Alice", "Smith"));
           userRatingRepository.save(new Customer("Bob", "Smith"));

           // fetch all customers
           System.out.println("Customers found with findAll():");
           System.out.println("-------------------------------");
           for (Customer customer : userRatingRepository.findAll()) {
               System.out.println(customer);
           }
           System.out.println();


            DataModel model = new FileDataModel(new File("../dataset.csv"));
            UserSimilarity similarity=new TanimotoCoefficientSimilarity(model);
           // UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
            UserBasedRecommender recommended = new GenericUserBasedRecommender(model, neighborhood, similarity);
            List<RecommendedItem> recommendations = recommended.recommend(2, 3);
        /*for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }*/
        return recommendations;

    }
}
