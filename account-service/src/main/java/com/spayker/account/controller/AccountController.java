package com.spayker.account.controller;

import com.spayker.account.domain.Account;
import com.spayker.account.domain.User;
import com.spayker.account.dto.AccountDto;
import com.spayker.account.dto.mapper.AccountMapper;
import com.spayker.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.util.List;

/**
 *  A controller layer for accounts with all needed (for now) methods.
 *  Last ones are called when request handling starts happening.
 *  Requests come on correspond url that linked by RequestMapping annotation with an appropriate declared method below.
 **/
@RestController
public class AccountController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountMapper accountMapper;

	/**
	 *  Returns an Account instance found by name.
	 *  @param name Strign value to make search by name possible
	 *  @return found Account entity
	 **/
	@PreAuthorize("#oauth2.hasScope('server')")
	@RequestMapping(path = "/{name}", method = RequestMethod.GET)
	public ResponseEntity<List<Account>> getAccountByName(@PathVariable String name) {
		return new ResponseEntity<>(accountService.findAccountByName(name), HttpStatus.OK);
	}

	/**
	 *  Creates account by provided User instance.
	 *  @param accountDto - data container related to new account entity to be created
	 *  @return ResponseEntity with created Account (toDo: make return just id of created account)
	 **/
	@RequestMapping(path = "/", method = RequestMethod.POST)
	public ResponseEntity<Account> createNewAccount(@Valid @RequestBody AccountDto accountDto) {
		Account account = accountMapper.accountDtoToAccount(accountDto);
		User user = accountMapper.accountDtoToUser(accountDto);
		return new ResponseEntity<>(accountService.create(account, user), HttpStatus.CREATED);
	}

}
