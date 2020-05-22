package com.spayker.consumer.client;

import com.spayker.consumer.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "consumer-auth")
public interface AuthServiceClient {

	@RequestMapping(method = RequestMethod.POST, value = "/mservicet/users", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	void createUser(User user);

}