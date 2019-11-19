package com.example.chat.controller;

import com.example.chat.dao.UserDAO;
import com.example.chat.domain.Message;
import com.example.chat.domain.User;
import com.example.chat.dto.MessageDTO;
import com.example.chat.dto.UserDTO;
import com.example.chat.exception.EntityNotFoundException;
import com.example.chat.service.UserService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@RestController
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private Channel channel;

    @RequestMapping(value = "/users",
        method = RequestMethod.POST)
    public User createUser(@RequestBody UserDTO userDTO) throws IOException {
        User user = userService.createUser(userDTO);
        userService.bindUserToQueue(user);
        return user;
    }

    @RequestMapping(value = "/users/{id}",
        method = RequestMethod.POST)
    public Message sendMessage(@PathVariable(value = "id") Long id,
                               @RequestBody MessageDTO messageDTO) throws IOException {
        return userService.sendMessage(id, messageDTO);
    }

    @RequestMapping("/users/{id}")
    public SseEmitter getMessages(@PathVariable(value = "id") Long id, HttpSession session) throws IOException {
        try {
            userService.findSingle(id);
        } catch (EntityNotFoundException e) {
            UserDTO userDTO = new UserDTO();
            userDTO.setName(UUID.randomUUID().toString());
            User user = userService.createUser(userDTO);
            userService.bindUserToQueue(user);
        }

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        Thread thread = new Thread(() ->{
            try {
                boolean keep = true;
                while (keep){
                    Thread.sleep(1000);
                    try {
                        channel.queueDeclare(id.toString(), true, false, false, null);

                        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                            Message message = (Message) SerializationUtils.deserialize(delivery.getBody());
                            if (message != null) {
                                emitter.send(message);
                            }
                        };

                        channel.basicConsume(id.toString(), true, deliverCallback, consumerTag -> {});
                    } catch (ClientAbortException cae) {
                        keep = false;
                    }
                }
                emitter.complete();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        return emitter;
    }
}
