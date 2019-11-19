package com.example.chat.service;

import com.example.chat.dao.UserDAO;
import com.example.chat.domain.Message;
import com.example.chat.domain.User;
import com.example.chat.dto.MessageDTO;
import com.example.chat.dto.UserDTO;
import com.example.chat.exception.BadRequestException;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.io.IOException;

import static com.example.chat.misc.Constants.EXCHANGE_NAME;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private Channel channel;

    public User createUser(UserDTO userDTO) {
        validate(userDTO);
        User user = new User();
        user.setName(userDTO.getName());

        return userDAO.insert(user);
    }

    public void bindUserToQueue(User user) throws IOException {
        channel.queueDeclare(user.getId().toString(), true, false, false, null);
        channel.queueBind(user.getId().toString(), EXCHANGE_NAME, user.getId().toString());
    }

    public User findSingle(Long id) {
        return userDAO.findSingle(id);
    }

    private void validate(UserDTO userDTO) {
        if (userDTO.getName() == null || userDTO.getName().equals("")) {
            throw new BadRequestException("User name is required");
        }
    }

    public Message sendMessage(Long id, MessageDTO messageDTO) throws IOException {
        validate(messageDTO);
        Message message = new Message();
        message.setSender(messageDTO.getSender());
        message.setText(messageDTO.getText());
        message.setTimestamp(messageDTO.getTimestamp());
        byte[] data = SerializationUtils.serialize(message);

        channel.basicPublish(EXCHANGE_NAME, id.toString(), null, data);
        return message;
    }

    private void validate(MessageDTO messageDTO) {
        if (messageDTO.getSender() == null ||
                messageDTO.getText().isEmpty() ||
                messageDTO.getTimestamp() == null) {
            throw new BadRequestException("Sender, message and time are required");
        }
    }
}
