package com.spayker.consumer.service;

import com.spayker.consumer.client.AuthServiceClient;
import com.spayker.consumer.domain.Consumer;
import com.spayker.consumer.domain.User;
import com.spayker.consumer.exception.ConsumerException;
import com.spayker.consumer.repository.ConsumerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ConsumerServiceImpl implements ConsumerService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ConsumerRepository repository;

	@Autowired
	private AuthServiceClient authClient;

	@Override
	public Consumer findByConsumerId(Long consumerId) {
		return repository.findById(consumerId).orElse(null);
	}

	@Override
	public List<Consumer> findConsumerByName(String name) {
		if(name.isEmpty() || name.isBlank()){
			throw new IllegalArgumentException("provided name is empty or blank");
		}
		return repository.findByName(name);
	}

	@Override
	public Consumer findConsumerByEmail(String email) {
		if(email.isEmpty() || email.isBlank()){
			throw new IllegalArgumentException("provided email is empty or blank");
		}
		return repository.findByEmail(email);
	}

	@Override
	public List<Consumer> findConsumerByCreatedDate(Date createdDate) {
		return repository.findByCreatedDate(createdDate);
	}

	@Override
	public List<Consumer> findConsumerByModifiedDate(Date modifiedDate) {
		return repository.findByModifiedDate(modifiedDate);
	}

	@Override
	public Consumer create(Consumer consumer, User user) {
		Consumer existing = repository.findByEmail(consumer.getEmail());
		if(existing == null){
			authClient.createUser(user);
			Consumer savedConsumer = repository.saveAndFlush(consumer);
			log.info("new consumer has been created: " + savedConsumer.getEmail());
			return savedConsumer;
		} else {
			throw new ConsumerException("consumer already exists: " + consumer.getEmail());
		}
	}

	@Override
	public Consumer saveChanges(Consumer update) {
		Consumer consumer = repository.findByEmail(update.getEmail());
		if(consumer == null){
			throw new ConsumerException("can't find consumer with email " + update.getEmail());
		} else {
			update.setModifiedDate(new Date());
			log.debug("consumer {} changes have been saved", update.getEmail());
			return repository.saveAndFlush(update);
		}
	}
}
