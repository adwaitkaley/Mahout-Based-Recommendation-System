package main;

/**
 * Created by Adwait on 11/11/2015.
 */


import com.mongodb.MongoException;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@Repository
@Configuration
@RestController
public class Controller {

    @Autowired
    private RetailerRepository retailerRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value="/recommend",method= RequestMethod.GET)
    public List<RecommendedItem> getRecommendation() throws Exception
    {


      /*  retailerRepository.save(new Retailer(1,"BestBuy","365 Fulton Court","El Paso","TX","United States",79999));

        for (Retailer retailer : retailerRepository.findAll())
        {
            System.out.println(retailer);
        }
*/

        DataModel model = new FileDataModel(new File("../dataset.csv"));
       // UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserSimilarity similarity=new TanimotoCoefficientSimilarity(model);
        UserNeighborhood neighborhood =new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommended = new GenericUserBasedRecommender(model, neighborhood, similarity);
        List<RecommendedItem> recommendations = recommended.recommend(3,2);
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }

        return recommendations;

    }

    @RequestMapping(value="/retailer",method=RequestMethod.POST)
    public ResponseEntity addRetailer(@RequestBody Retailer bodyelements)
    {
        boolean pollAdded=false;
        try
        {
            retailerRepository.save(bodyelements);
            pollAdded=true;
        }catch(MongoException e)
        {

        }
        if(pollAdded)
            return new ResponseEntity (bodyelements,HttpStatus.CREATED);
        else
            return new ResponseEntity ("Unable to add Retailer.Please Try again Later !!!",HttpStatus.BAD_GATEWAY);

    }

    @RequestMapping(value="/retailer",method=RequestMethod.GET)
    public ResponseEntity getRetailer()
    {
            return new ResponseEntity (retailerRepository.findAll(),HttpStatus.OK);
    }

    @RequestMapping(value="/user",method=RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody User bodyelements)
    {
        /*new User("1", "Adwait", "10/10/1990","test@test.com", "M", "101 E San Sernando Street","San Jose","CA", "US", "95112");*/
        userRepository.save(bodyelements);
        return new ResponseEntity ("Created",HttpStatus.CREATED);
    }
    @RequestMapping(value="/user",method=RequestMethod.GET)
    public ResponseEntity getUsers()
    {
        /*new User("1", "Adwait", "10/10/1990","test@test.com", "M", "101 E San Sernando Street","San Jose","CA", "US", "95112");*/
        //userRepository.save(bodyelements);
        return new ResponseEntity (userRepository.findAll(),HttpStatus.CREATED);
    }




}
