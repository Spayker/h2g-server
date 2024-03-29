package com.spayker.consumer.service.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedAuthoritiesExtractor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Extended implementation of {@link org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices}
 *
 * By default, it designed to return only user details. This class provides {@link #getRequest(Map)} method, which
 * returns clientId and scope of calling service. This information used in controller's security checks.
 */
public class CustomUserInfoTokenServices implements ResourceServerTokenServices {

	private final Log logger = LogFactory.getLog(getClass());

	private static final String[] PRINCIPAL_KEYS = new String[] { "user", "username",
			"userid", "user_id", "login", "id", "name" };

	private final String userInfoEndpointUrl;

	private final String clientId;

	private OAuth2RestOperations restTemplate;

	private String tokenType = DefaultOAuth2AccessToken.BEARER_TYPE;

	private AuthoritiesExtractor authoritiesExtractor = new FixedAuthoritiesExtractor();

	public CustomUserInfoTokenServices(String userInfoEndpointUrl, String clientId) {
		this.userInfoEndpointUrl = userInfoEndpointUrl;
		this.clientId = clientId;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public void setRestTemplate(OAuth2RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setAuthoritiesExtractor(AuthoritiesExtractor authoritiesExtractor) {
		this.authoritiesExtractor = authoritiesExtractor;
	}

	/**
	 *  An overridden method to load the credentials for the specified access token.
	 *  @param accessToken - provided String value which represents token
	 *  @return OAuth2Authentication - authentication for the access token.
	 *  @throws AuthenticationException if the access token is expired
	 *  @throws InvalidTokenException if the token isn't valid
	 **/
	@Override
	public OAuth2Authentication loadAuthentication(String accessToken)
			throws AuthenticationException, InvalidTokenException {
		Map<String, Object> map = getMap(userInfoEndpointUrl, accessToken);
		if (map.containsKey("error")) {
			logger.debug("userinfo returned error: " + map.get("error"));
			throw new InvalidTokenException(accessToken);
		}
		return extractAuthentication(map);
	}

	/**
	 *  Returns  OAuth2Authentication instance created by OAuth2Request and UsernamePasswordAuthenticationToken objects.
	 *  @param map - container with principal and request data inside
	 *  @return OAuth2Authentication - authentication for the access token.
	 **/
	private OAuth2Authentication extractAuthentication(Map<String, Object> map) {
		Object principal = getPrincipal(map);
		OAuth2Request request = getRequest(map);
		List<GrantedAuthority> authorities = authoritiesExtractor.extractAuthorities(map);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				principal, "N/A", authorities);
		token.setDetails(map);
		return new OAuth2Authentication(request, token);
	}

	/**
	 *  Creates and returns OAuth2Request instance by provided Map data container.
	 *  @param map - container with principal and request data inside
	 *  @return OAuth2Request instance
	 **/
	private Object getPrincipal(Map<String, Object> map) {
		for (String key : PRINCIPAL_KEYS) {
			if (map.containsKey(key)) {
				return map.get(key);
			}
		}
		return "unknown";
	}

	/**
	 *  Extracts Principal object from map container by PRINCIPAL key.
	 *  @param map - container with principal and request data inside
	 *  @return - found principal instance or unknown string
	 **/
	@SuppressWarnings({ "unchecked" })
	private OAuth2Request getRequest(Map<String, Object> map) {
		Map<String, Object> request = (Map<String, Object>) map.get("oauth2Request");

		String clientId = (String) request.get("clientId");
		Set<String> scope = new LinkedHashSet<>(request.containsKey("scope") ?
				(Collection<String>) request.get("scope") : Collections.<String>emptySet());

		return new OAuth2Request(null, clientId, null, true, new HashSet<>(scope),
				null, null, null, null);
	}

	/**
	 *  Stub method. Originally must generate OAuth2AccessToken according to provided accessToken.
	 *  @param accessToken - received accessToken to be checked
	 *  @return OAuth2AccessToken
	 **/
	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		throw new UnsupportedOperationException("Not supported: read access token");
	}

	/**
	 *  Returns Map container that contains body information provided by a request in its body.
	 *  @param path - String value to where current request has come
	 *  @param accessToken - provided String value which represents token
	 *  @return Map container with principal and request data inside
	 **/
	@SuppressWarnings({ "unchecked" })
	private Map<String, Object> getMap(String path, String accessToken) {
		logger.debug("Getting user info from: " + path);
		try {
			if (restTemplate == null) {
				BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
				resource.setClientId(clientId);
				restTemplate = new OAuth2RestTemplate(resource);
			}
			OAuth2AccessToken existingToken = restTemplate.getOAuth2ClientContext()
					.getAccessToken();
			if (existingToken == null || !accessToken.equals(existingToken.getValue())) {
				DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(
						accessToken);
				token.setTokenType(tokenType);
				restTemplate.getOAuth2ClientContext().setAccessToken(token);
			}
			return restTemplate.getForEntity(path, Map.class).getBody();
		} catch (Exception ex) {
			logger.info("Could not fetch user details: " + ex.getClass() + ", "
					+ ex.getMessage());
			return Collections.<String, Object>singletonMap("error",
					"Could not fetch user details");
		}
	}
}
