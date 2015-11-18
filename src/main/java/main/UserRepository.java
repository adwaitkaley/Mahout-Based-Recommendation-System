package main;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Adwait on 11/15/2015.
 */
public interface UserRepository extends MongoRepository<User,String> {
    public User findByUserName(String userName);
}
