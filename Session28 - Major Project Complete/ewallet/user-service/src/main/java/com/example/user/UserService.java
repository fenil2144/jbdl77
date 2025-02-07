package com.example.user;

import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.example.user.UserConstants.USER_AUTHORITY;
import static com.example.wallet.CommonConstants.USER_CREATED_TOPIC;
import static com.example.wallet.CommonConstants.USER_CREATED_TOPIC_EMAIL;
import static com.example.wallet.CommonConstants.USER_CREATED_TOPIC_IDENTIFIER_KEY;
import static com.example.wallet.CommonConstants.USER_CREATED_TOPIC_IDENTIFIER_VALUE;
import static com.example.wallet.CommonConstants.USER_CREATED_TOPIC_PHONE_NUMBER;
import static com.example.wallet.CommonConstants.USER_CREATED_TOPIC_USER_ID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(username);
    }

    public void createUser(UserCreateRequest userCreateRequest) throws JsonProcessingException {

        User user = userCreateRequest.toUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAuthorities(USER_AUTHORITY);
        user = userRepository.save(user);

        // publish the event post user creation which will be listened by consumers
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(USER_CREATED_TOPIC_USER_ID, user.getId());
        jsonObject.put(USER_CREATED_TOPIC_PHONE_NUMBER, user.getPhoneNumber());
        jsonObject.put(USER_CREATED_TOPIC_EMAIL, user.getEmail());
        jsonObject.put(USER_CREATED_TOPIC_IDENTIFIER_KEY, user.getUserIdentifier());
        jsonObject.put(USER_CREATED_TOPIC_IDENTIFIER_VALUE, user.getIdentifierValue());

        kafkaTemplate.send(USER_CREATED_TOPIC, objectMapper.writeValueAsString(jsonObject));
    }

    public List<User> getAllUserDetails(){
        return userRepository.findAll();
    }
}
