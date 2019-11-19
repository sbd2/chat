package com.example.chat;

import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import static com.example.chat.misc.Constants.EXCHANGE_NAME;
import static com.example.chat.misc.Constants.EXCHANGE_TYPE_DIRECT;

@SpringBootApplication
public class ChatApplication {

    @Autowired
    private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(ChatApplication.class, args);
	}

	@Bean
	public ConnectionFactory connectionFactory() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
		ConnectionFactory factory = new ConnectionFactory();
		String username = env.getProperty("could.username");
		String password = env.getProperty("could.password");
		factory.setUri("amqp://" + username + ":" + password + "@eagle.rmq.cloudamqp.com/" + username);
		factory.setAutomaticRecoveryEnabled(true);
		return factory;
	}

	@Bean
	public Connection connection() throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
		return connectionFactory().newConnection();
	}

	@Bean
	public Channel channel() throws URISyntaxException, KeyManagementException, TimeoutException, NoSuchAlgorithmException, IOException {
		Channel channel = connection().createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE_DIRECT, true);
		channel.addShutdownListener(Throwable::printStackTrace);
		return channel;
	}
}
