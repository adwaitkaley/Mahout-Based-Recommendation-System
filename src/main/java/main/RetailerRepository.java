package main;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Adwait on 11/15/2015.
 */
public interface RetailerRepository extends MongoRepository<Retailer, String>
{
    public Retailer findByStoreName(String storeName);
 //   public List<Customer> findByLastName(String lastName);
}
