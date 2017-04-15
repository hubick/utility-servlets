/*
 * Copyright 2007-2017 by Chris Hubick. All Rights Reserved.
 * 
 * This work is licensed under the terms of the "GNU AFFERO GENERAL PUBLIC LICENSE" version 3, as published by the Free
 * Software Foundation <http://www.gnu.org/licenses/>, a copy of which you should have received in the file LICENSE.txt.
 */

package net.www_eee.util.servlets.role;

import java.io.*;

import org.eclipse.jdt.annotation.*;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Populate clients into a {@linkplain HttpServletRequest#isUserInRole(String) role} based on if their user
 * {@linkplain HttpServletRequest#getRemoteUser() login} is known or unknown.
 * 
 * @see HttpServletRequest#getRemoteUser()
 * @see HttpServletRequest#isUserInRole(String)
 */
@NonNullByDefault
public class KnownUnknownUserRoleFilter implements Filter {
  /**
   * The name of the {@linkplain FilterConfig#getInitParameter(String) configuration parameter} whose value specifies
   * the {@linkplain HttpServletRequest#isUserInRole(String) role} to populate clients into if their
   * {@linkplain HttpServletRequest#getRemoteUser() login} is not <code>null</code>. If not set, defaults to
   * <code>"known-user"</code>.
   */
  public static final String KNOWN_USER_ROLE_PROP = KnownUnknownUserRoleFilter.class.getSimpleName() + ".KnownUserRole";
  /**
   * The name of the {@linkplain FilterConfig#getInitParameter(String) configuration parameter} whose value specifies
   * the {@linkplain HttpServletRequest#isUserInRole(String) role} to populate clients into if their
   * {@linkplain HttpServletRequest#getRemoteUser() login} is <code>null</code>. If not set, defaults to
   * <code>"unknown-user"</code>.
   */
  public static final String UNKNOWN_USER_ROLE_PROP = KnownUnknownUserRoleFilter.class.getSimpleName() + ".UnknownUserRole";
  /**
   * @see #KNOWN_USER_ROLE_PROP
   */
  protected String knownUserRole = "known-user";
  /**
   * @see #UNKNOWN_USER_ROLE_PROP
   */
  protected String unknownUserRole = "unknown-user";

  @Override
  public void init(final FilterConfig filterConfig) {
    final String knownUserRoleProp = filterConfig.getInitParameter(KNOWN_USER_ROLE_PROP);
    if (knownUserRoleProp != null) knownUserRole = knownUserRoleProp;
    final String unknownUserRoleProp = filterConfig.getInitParameter(UNKNOWN_USER_ROLE_PROP);
    if (unknownUserRoleProp != null) unknownUserRole = unknownUserRoleProp;
    return;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws ServletException, IOException {
    final String remoteUser = ((HttpServletRequest)servletRequest).getRemoteUser();
    if (remoteUser != null) {
      servletRequest = new UserRoleRequestWrapper((HttpServletRequest)servletRequest, knownUserRole);
    } else {
      servletRequest = new UserRoleRequestWrapper((HttpServletRequest)servletRequest, unknownUserRole);
    }
    filterChain.doFilter(servletRequest, servletResponse);
    return;
  }

  @Override
  public void destroy() {
    return;
  }

}
