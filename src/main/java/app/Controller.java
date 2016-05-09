package app;

/**
 * Created by Adwait on 11/11/2015.
 */

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import mongo.MongoClientMain;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.BooleanPreference;
import org.apache.mahout.cf.taste.impl.model.BooleanUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Repository
@Configuration
@RestController
public class Controller {

    @Autowired
    private RetailerRepository retailerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopListRepository shopListRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CheckInRepository checkInRepository;


    /*
     * Generates Recommendation for a given user
     */
    @RequestMapping(value="/recommend",method= RequestMethod.GET)
    public ResponseEntity getRecommendation(@RequestParam("userId")  long uid,@RequestParam("storeId")  int storeId) throws Exception
    {
        FastByIDMap<PreferenceArray> preferenceArrayFastByIDMap=new FastByIDMap<PreferenceArray>();

        DB database=new MongoClientMain().getConnection();
        DBCollection coll= database.getCollection("shopList");
        List<Long> userIds=coll.distinct("userId");

        BooleanUserPreferenceArray user=null;
        User currentUser=userRepository.findByUserId(uid);
        List<User> similarUserList= userRepository.findByUserProfessionAndUserClothingProfessionalAndUserClothingPersonalAndUserHobbiesAndUserSpendingStyle(
                currentUser.getUserProfession(),
                currentUser.getUserClothingProfessional(),
                currentUser.getUserClothingPersonal(),
                currentUser.getUserHobbies(),
                currentUser.getUserSpendingStyle()
        );

        for(int i=0;i<similarUserList.size();i++)
        {
            int preferenceCount=0;
            List<ShopList> shopList=shopListRepository.findByUserId(userIds.get(i));
            user=new BooleanUserPreferenceArray(shopList.size());
            for(ShopList shop:shopList)
            {
                    user.set(preferenceCount, new BooleanPreference(shop.getUserId(), shop.getItemId()));
                    preferenceCount++;
            }
            preferenceArrayFastByIDMap.put(userIds.get(i),user);
        }


        DataModel model=new GenericDataModel(preferenceArrayFastByIDMap);


        //Similarity Method Used
        UserSimilarity similarity=new TanimotoCoefficientSimilarity(model);


        //First parameter is the number of nearest neighbour similar users
       UserNeighborhood neighborhood =new NearestNUserNeighborhood(5, similarity, model);

        //BooleanRecommender as no weightage of preferences is considered
        UserBasedRecommender recommended = new GenericBooleanPrefUserBasedRecommender(model, neighborhood, similarity);

       //Recommendations for ( userid ,Number of recommendations Needed )
        List<RecommendedItem> recommendations = recommended.recommend(uid,10);

        ArrayList<Item> recommendedItemList=new ArrayList<Item>();
        for (RecommendedItem recommendation : recommendations)
        {
            Item item=itemRepository.findByItemId(recommendation.getItemID());
            recommendedItemList.add(item);
        }

        return new ResponseEntity(recommendedItemList,HttpStatus.OK);

    }

    /*
    * The following method creates a Retailer in the database
    **/
    @RequestMapping(value="/retailer",method=RequestMethod.POST,consumes="application/json")
    public ResponseEntity addRetailer(@RequestBody Retailer bodyelements) throws JSONException {
        boolean retailerAdded=false;
        try
        {
            retailerRepository.save(bodyelements);
            retailerAdded=true;
        }catch(MongoException e)
        {
            e.printStackTrace();
        }

        if(retailerAdded)
        {
            return new ResponseEntity (retailerRepository.findByStoreId(bodyelements.getStoreId()),HttpStatus.CREATED);
        }

        else
        {
            return new ResponseEntity (new Response("Unable to add Retailer.Please Try again Later !!!"),HttpStatus.NOT_FOUND);
        }


    }

 /*
  * The following method updates a Retailer in the database
  **/
    @RequestMapping(value="/retailer/{id}",method=RequestMethod.PUT,consumes="application/json")
    public ResponseEntity updateRetailer(@PathVariable("id") int id,@RequestBody Retailer bodyelements) throws JSONException {
        boolean retailerUpdated=false;
        try
        {
            Retailer retailer=null;
            retailer=retailerRepository.findByStoreId(id);
            if(retailer!=null)
            {
                retailer.setStoreCity(bodyelements.getStoreCity());
                retailer.setStoreCountry(bodyelements.getStoreCountry());
                retailer.setStoreName(bodyelements.getStoreName());
                retailer.setStoreState(bodyelements.getStoreState());
                retailer.setStoreStreet(bodyelements.getStoreStreet());
                retailer.setStoreZip(bodyelements.getStoreZip());
                retailerRepository.save(retailer);
                retailerUpdated=true;
            }

        }catch(MongoException e)
        {
            e.printStackTrace();
        }
        if(retailerUpdated)
        {
            return new ResponseEntity (new Response("Retailer Information updated Successfully !!!"),HttpStatus.OK);
        }

        else
        {
            return new ResponseEntity (new Response("Retailer Information updated Successfully !!!"),HttpStatus.NOT_FOUND);
        }

    }


  /*
  * The following method gets a Retailer in the database
  **/
    @RequestMapping(value="/retailer",method=RequestMethod.GET,consumes="application/json")
    public ResponseEntity getRetailer(@RequestParam("retailerId") int retailerId ) throws JSONException {
        Retailer retailer=null;
        retailer=retailerRepository.findByStoreId(retailerId);

        if(retailer==null)
        {
            return new ResponseEntity (new Response("Unable to find Retailer with StoreId"+retailerId),HttpStatus.NOT_FOUND);
        }

        else
        {
            return new ResponseEntity (retailer,HttpStatus.OK);
        }
    }

    /*
  * The following method deletes a Retailer in the database
  **/
    @RequestMapping(value="/retailer",method=RequestMethod.DELETE,consumes="application/json")
    public ResponseEntity deleteRetailer(@RequestParam("retailerId") int retailerId )
    {
        Retailer retailer=null;
        retailer=retailerRepository.findByStoreId(retailerId);
        if(retailer==null)
            return new ResponseEntity (new Response("Cannot Find Retailer with RetailerId : "+retailerId),HttpStatus.NOT_FOUND);
        else
        {
            retailerRepository.delete(retailer.getId());
            return new ResponseEntity (new Response("Deleted Retailer with RetailerId : "+retailerId),HttpStatus.OK);
        }

    }

  /*
  * The following method gets all Retailers from the database
  **/

    @RequestMapping(value="/retailers",method=RequestMethod.GET,consumes="application/json")
    public ResponseEntity getAllRetailers()
    {
            return new ResponseEntity (retailerRepository.findAll(),HttpStatus.OK);
    }

    /*
    * The following method creates a User in the database
    **/
    @RequestMapping(value="/user",method=RequestMethod.POST,consumes="application/json")
    public ResponseEntity addUser(@RequestBody User bodyelements)
    {
        boolean userAdded=false;
        try
        {
            userRepository.save(bodyelements);
            userAdded=true;
        }catch(MongoException e)
        {
           e.printStackTrace();
        }
        if(userAdded)
            return new ResponseEntity (userRepository.findByUserId(bodyelements.getUserId()),HttpStatus.CREATED);
        else
            return new ResponseEntity (new Response("Unable to add User !!! Please Try Again Later !!!"),HttpStatus.NOT_FOUND);
    }

    /*
     * The following method updates a Retailer in the database
     **/
    @RequestMapping(value="/user/{id}",method=RequestMethod.PUT,consumes="application/json")
    public ResponseEntity updateUser(@PathVariable("id") long id,@RequestBody User bodyelements)
    {
        boolean userUpdated=false;
        try
        {
            User user=null;
            user=userRepository.findByUserId(id);
            if(user!=null)
            {
                user.setUserCity(bodyelements.getUserCity());
                user.setUserCountry(bodyelements.getUserCountry());
                user.setUserDob(bodyelements.getUserDob());
                user.setUserEmail(bodyelements.getUserEmail());
                user.setUserGender(bodyelements.getUserGender());
                user.setUserName(bodyelements.getUserName());
                user.setUserProfession(bodyelements.getUserProfession());
                user.setUserClothingProfessional(bodyelements.getUserClothingProfessional());
                user.setUserClothingPersonal(bodyelements.getUserClothingPersonal());
                user.setUserHobbies(bodyelements.getUserHobbies());
                user.setUserSpendingStyle(bodyelements.getUserSpendingStyle());
                userRepository.save(user);
                userUpdated=true;
            }

        }catch(MongoException e)
        {

        }
        if(userUpdated)
            return new ResponseEntity (new Response("User Information updated Successfully !!!"),HttpStatus.CREATED);
        else
            return new ResponseEntity (new Response("Unable to update User.Please Try again Later !!!"),HttpStatus.NOT_FOUND);

    }


    /*
    * The following method gets a User from the database
    **/
    @RequestMapping(value="/user",method=RequestMethod.GET,consumes="application/json")
    public ResponseEntity getUser(@RequestParam("userId") long userId )
    {
        User user=null;
        user=userRepository.findByUserId(userId);
        if(user==null)
            return new ResponseEntity (new Response("Cannot Find User with UserId : "+userId),HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity (user,HttpStatus.OK);
    }

    /*
  * The following method deletes a User in the database
  **/
    @RequestMapping(value="/user",method=RequestMethod.DELETE,consumes="application/json")
    public ResponseEntity deleteUser(@RequestParam("userId") long userId )
    {
        User user=null;
        user=userRepository.findByUserId(userId);
        if(user==null)
            return new ResponseEntity (new Response("Cannot Find User with UserId : "+userId),HttpStatus.NOT_FOUND);
        else
        {
            userRepository.delete(user.getId());
            return new ResponseEntity (new Response("Deleted User with UserId : "+userId),HttpStatus.OK);
        }

    }

  /*
  * The following method gets all Users from the database
  **/

    @RequestMapping(value="/users",method=RequestMethod.GET,consumes="application/json")
    public ResponseEntity getAllUsers()
    {
        return new ResponseEntity (userRepository.findAll(),HttpStatus.OK);
    }



    /*
   * The following method creates a Item in the database
   **/
    @RequestMapping(value="/item",method=RequestMethod.POST,consumes="application/json")
    public ResponseEntity addItem(@RequestBody Item bodyelements)
    {
        boolean itemAdded=false;
        try
        {
            itemRepository.save(bodyelements);
            itemAdded=true;
        }catch(MongoException e)
        {

        }
        if(itemAdded)
            return new ResponseEntity (itemRepository.findByItemId(bodyelements.getItemId()),HttpStatus.CREATED);
        else
            return new ResponseEntity (new Response("Unable to add Item.Please Try again Later !!!"),HttpStatus.NOT_FOUND);

    }

    /*
     * The following method updates a Item in the database
     **/
    @RequestMapping(value="/item/{id}",method=RequestMethod.PUT,consumes="application/json")
    public ResponseEntity updateItem(@PathVariable("id") long id,@RequestBody Item bodyelements)
    {
        boolean itemUpdated=false;
        try
        {
            Item item=null;
            item=itemRepository.findByItemId(id);
            if(item!=null)
            {
                item.setItemCategory(bodyelements.getItemCategory());
                item.setItemDepartment(bodyelements.getItemDepartment());
                item.setItemName(bodyelements.getItemName());
                item.setItemShortDescription(bodyelements.getItemShortDescription());
                item.setItemMediumImage(bodyelements.getItemMediumImage());
                item.setItemRestrictedSalePrice(bodyelements.getItemRestrictedSalePrice());
                item.setStoreId(bodyelements.getStoreId());
                itemRepository.save(item);
                itemUpdated=true;
            }

        }catch(MongoException e)
        {
            e.printStackTrace();
        }
        if(itemUpdated)
            return new ResponseEntity (new Response("Item Information updated Successfully for ItemID :"+id),HttpStatus.OK);
        else
            return new ResponseEntity (new Response("Unable to update Item with ItemID : "+id+".Please Try again Later !!!"),HttpStatus.NOT_FOUND);

    }


    /*
    * The following method gets a Item from the database
    **/
    @RequestMapping(value="/item",method=RequestMethod.GET,consumes="application/json")
    public ResponseEntity getItem(@RequestParam("itemId") long itemId )
    {
        Item item=null;
        item=itemRepository.findByItemId(itemId);
        if(item==null)
            return new ResponseEntity (new Response("Cannot Find User with ItemId : "+itemId),HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity (item,HttpStatus.OK);
    }

    /*
  * The following method deletes a Item in the database
  **/
    @RequestMapping(value="/item",method=RequestMethod.DELETE,consumes="application/json")
    public ResponseEntity deleteItem(@RequestParam("itemId") long itemId )
    {
        Item item=null;
        item=itemRepository.findByItemId(itemId);
        if(item==null)
            return new ResponseEntity (new Response("Cannot Find Item with ItemId : "+itemId),HttpStatus.NOT_FOUND);
        else
        {
            itemRepository.delete(item.getId());
            return new ResponseEntity (new Response("Deleted Item with ItemId : "+itemId),HttpStatus.OK);
        }

    }

  /*
  * The following method gets all Items from the database
  **/

    @RequestMapping(value="/items",method=RequestMethod.GET,consumes="application/json")
    public ResponseEntity getAllItems()
    {
        return new ResponseEntity (itemRepository.findAll(),HttpStatus.OK);
    }


  /*
  * The following method sets Items from User to Shopping List in the database
  **/
    @RequestMapping(value="/shoplist",method=RequestMethod.POST,consumes="application/json")
    public ResponseEntity setShopListItem(@RequestBody ShopList bodyelements)
    {
        boolean itemAdded=false;
        try {
            shopListRepository.save(bodyelements);
            itemAdded=true;
        }
        catch (MongoException e)
        {
            e.printStackTrace();
        }
        if(itemAdded)
            return new ResponseEntity (new Response("ShopListItem Added Successfully for UserID : "+bodyelements.getUserId()),HttpStatus.OK);
        else
            return new ResponseEntity (new Response("Adding ShopListItem Failed  for UserID : "+bodyelements.getUserId()),HttpStatus.NOT_FOUND);
    }


    /*
  * The following method adds Checkins for User in the database
  **/
    @RequestMapping(value="/checkin",method=RequestMethod.POST,consumes="application/json")
    public ResponseEntity setCheckIns(@RequestBody CheckIn bodyelements)
    {
        boolean checkInsAdded=false;
        int userId=bodyelements.getUserId();
        String checkins[]=bodyelements.getDescription().split(",");
        try {

            for(int i=0;i<checkins.length;i++)
            {
                checkInRepository.save(new CheckIn(userId,checkins[i]));
            }
            checkInsAdded=true;
        }
        catch (MongoException e)
        {
            e.printStackTrace();
        }
        if(checkInsAdded)
            return new ResponseEntity (new Response("Checkins Added Successfully for UserID : "+userId),HttpStatus.OK);
        else
            return new ResponseEntity (new Response("Adding Checkins Failed  for UserID : "+userId),HttpStatus.NOT_FOUND);
    }

    /*
  * The following method adds product in the shoplist of buyer
  **/
    @RequestMapping(value="/buy",method=RequestMethod.POST,consumes="application/json")
    public ResponseEntity buyProducts(@RequestBody ShopList bodyelements)
    {

        boolean listUpdated=false;
        try
        {
            shopListRepository.save(bodyelements);
            listUpdated=true;
        }catch (MongoException e)
        {
            e.printStackTrace();
        }

        if(listUpdated)
            return new ResponseEntity (bodyelements,HttpStatus.OK);
        else
            return new ResponseEntity (new Response("Adding Checkins Failed  for UserID : "+bodyelements.getUserId()),HttpStatus.NOT_FOUND);
    }

    /*
  * The following method returns purchase history for the user
  **/
    @RequestMapping(value="/purchaseHistory",method=RequestMethod.GET,consumes="application/json")
    public ResponseEntity getPurchaseHistory(@RequestParam("userId")long userId)
    {
        List<ShopList> shopList=shopListRepository.findByUserId(userId);
        ArrayList<PurchasedItems> purchasedItemsArrayList=new ArrayList<PurchasedItems>();
        Item item=new Item();
        for(ShopList shop:shopList)
        {
            long itemId=shop.getItemId();
            item=itemRepository.findByItemId(itemId);
            PurchasedItems purchasedItems=new PurchasedItems(item.getItemName(),item.getStoreId(),item.getItemRestrictedSalePrice());
            purchasedItemsArrayList.add(purchasedItems);
        }
        return new ResponseEntity(purchasedItemsArrayList,HttpStatus.OK);
    }


}
