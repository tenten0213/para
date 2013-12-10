/*
 * Copyright 2013 Alex Bogdanovski <albogdano@me.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You can reach the author at: https://github.com/albogdano
 */
package com.erudika.para.security;

import com.erudika.para.core.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 *
 * @author Alex Bogdanovski <albogdano@me.com>
 */
public class PasswordAuthFilter extends AbstractAuthenticationProcessingFilter {

	public static final String PASSWORD_ACTION = "password_auth";
	private final String PASSWORD = "password";
	private final String EMAIL = "email";
	
	public PasswordAuthFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}
		
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) 
			throws AuthenticationException, IOException, ServletException {
		String requestURI = request.getRequestURI();
		Authentication userAuth = null;
		User user = new User();
		
		if(requestURI.endsWith(PASSWORD_ACTION)){
			user.setIdentifier(request.getParameter(EMAIL));
			user.setPassword(request.getParameter(PASSWORD));
			
			if(User.passwordMatches(user) && StringUtils.contains(user.getIdentifier(), "@")){
				//success!
				user = User.readUserForIdentifier(user);
				userAuth = new UserAuthentication(user);
			}
		}
		
		if(userAuth == null || user == null || user.getIdentifier() == null) {
			throw new BadCredentialsException("Bad credentials.");
		} else if(!user.isEnabled()) {
			throw new LockedException("Account is locked.");
//		} else {
//			SecurityUtils.setAuthCookie(user, request, response);
		}
		return userAuth;
	}
	
}
