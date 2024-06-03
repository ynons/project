package yinonx.apitest.repos;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import yinonx.apitest.classes.User;


@Repository
public interface UserRepository extends MongoRepository<User, Long>
{
   public User findByUn(String un);
   public User findById(long id);
   public List<User> findAll();
   
}
