package com.spayker.account.service;

import com.spayker.account.domain.Account;
import com.spayker.account.domain.User;

import java.util.Date;
import java.util.List;

/**
 *  Service layer interface to provided API for work with Account entity.
 **/
public interface AccountService {

	/**
	 *  Looks for stored account by its id.
	 *  @param accountId - string value for search
	 *  @return found account
	 **/
	Account findAccountById(String accountId);

	/**
	 *  Looks for stored account by its name.
	 *  @param accountName - string value for search
	 *  @return list of found accounts
	 **/
	List<Account> findAccountByName(String accountName);

	/**
	 *  Looks for stored account by its email
	 *  @param email - string value for search
	 *  @return found Account
	 **/
	Account findAccountByEmail(String email);

	/**
	 *  Looks for stored account by its created date
	 *  @param createdDate - string value for search
	 *  @return list of found accounts
	 **/
	List<Account> findAccountByCreatedDate(Date createdDate);

	/**
	 *  Looks for stored account by its modified date
	 *  @param modifiedDate - string value for search
	 *  @return list of found accounts
	 **/
	List<Account> findAccountByModifiedDate(Date modifiedDate);

	/**
	 *  Looks for stored account by its modified age
	 *  @param age - string value for search
	 *  @return list of found accounts
	 **/
	List<Account> findAccountByAge(int age);

	/**
	 *  Looks for stored account by its gender
	 *  @param gender - string value for search
	 *  @return list of found accounts
	 **/
	List<Account> findAccountByGender(int gender);

	/**
	 *  Looks for stored account by its weight
	 *  @param weight - string value for search
	 *  @return list of found accounts
	 **/
	List<Account> findAccountByWeight(int weight);

	/**
	 *  Looks for stored account by its height
	 *  @param height - string value for search
	 *  @return list of found accounts
	 **/
	List<Account> findAccountByHeight(int height);

	/**
	 *  Creates new Account and returns it by provided User instance.
	 *  @param user - instance of User with email and password
	 *  @return created Account
	 **/
	Account create(Account account, User user);

	/**
	 *  Updates a stored account and returns its updated variant.
	 *  @param update - an updated variation of Account that must be persisted
	 **/
	Account saveChanges(Account update);
}
