package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import com.example.demo.model.User;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Service
public class UserService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public String validateUser(String username, String password) {
        Query query = new Query(Criteria.where("username").is(username).and("password").is(password));
        User user = mongoTemplate.findOne(query, User.class);
        if (user != null) {
            return "success";
        }
        return "failed";
    }

    public User validateUserOTP(String phoneNumber, String otp) {
        Query query = new Query(Criteria.where("phoneNumber").is(phoneNumber).and("otp").is(otp));
        return mongoTemplate.findOne(query, User.class);
    }

    public User createUser(User user) {
        return mongoTemplate.save(user);
    }
}