/*
 * Copyright 2007-2017 by Chris Hubick. All Rights Reserved.
 * 
 * This work is licensed under the terms of the "GNU AFFERO GENERAL PUBLIC LICENSE" version 3, as published by the Free
 * Software Foundation <http://www.gnu.org/licenses/>, a copy of which you should have received in the file LICENSE.txt.
 */

package net.www_eee.util.servlets.role;

import java.io.*;
import java.util.*;

import org.eclipse.jdt.annotation.*;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Populate clients into a {@linkplain #ROLES_PROP predefined} set of
 * {@linkplain HttpServletRequest#isUserInRole(String) roles}.
 * 
 * @see HttpServletRequest#isUserInRole(String)
 */
@NonNullByDefault
public class StaticUserRoleFilter implements Filter {
  /**
   * The name of the {@linkplain FilterConfig#getInitParameter(String) configuration parameter} (when prefixed with '
   * <code>StaticUserRoleFilter.</code>' + &lt;{@link FilterConfig#getFilterName() FilterName}&gt; + ' <code>.</code>')
   * whose value is a comma separated list of roles the client will be added to.
   */
  public static final String ROLES_PROP = "Roles";
  /**
   * @see #ROLES_PROP
   */
  protected @Nullable List<String> roles = null;

  @Override
  public void init(final FilterConfig filterConfig) {
    final String prefix = StaticUserRoleFilter.class.getSimpleName() + '.' + filterConfig.getFilterName() + '.';
    final String rolesProp = filterConfig.getInitParameter(prefix + ROLES_PROP);
    if (rolesProp != null) roles = Arrays.asList(rolesProp.trim().split("[\\s]*,[\\s]*"));
    return;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws ServletException, IOException {
    HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
    if (roles != null) {
      for (String role : roles) {
        httpServletRequest = new UserRoleRequestWrapper(httpServletRequest, role);
      }
    }
    filterChain.doFilter(httpServletRequest, servletResponse);
    return;
  }

  @Override
  public void destroy() {
    return;
  }

}
