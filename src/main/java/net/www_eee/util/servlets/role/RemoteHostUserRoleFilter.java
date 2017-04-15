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
 * Populate clients into a {@linkplain HttpServletRequest#isUserInRole(String) role} based on their
 * {@linkplain ServletRequest#getRemoteHost() remote host}.
 * 
 * @see ServletRequest#getRemoteHost()
 * @see HttpServletRequest#isUserInRole(String)
 */
@NonNullByDefault
public class RemoteHostUserRoleFilter implements Filter {
  /**
   * @see #ROLE_PREFIX_PROP
   */
  public static final String ROLE_PREFIX_DEFAULT = "remote-host-";
  /**
   * The name of the {@linkplain FilterConfig#getInitParameter(String) configuration parameter} whose value will prefix
   * the created roles.
   */
  public static final String ROLE_PREFIX_PROP = RemoteHostUserRoleFilter.class.getSimpleName() + ".Prefix";
  /**
   * @see #ROLE_PREFIX_PROP
   */
  protected String rolePrefix = ROLE_PREFIX_DEFAULT;

  @Override
  public void init(final FilterConfig filterConfig) {
    final String rolePrefixProp = filterConfig.getInitParameter(ROLE_PREFIX_PROP);
    if (rolePrefixProp != null) rolePrefix = rolePrefixProp;
    return;
  }

  @Override
  public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws ServletException, IOException {
    filterChain.doFilter(new UserRoleRequestWrapper((HttpServletRequest)servletRequest, rolePrefix + servletRequest.getRemoteHost()), servletResponse);
    return;
  }

  @Override
  public void destroy() {
    return;
  }

}
