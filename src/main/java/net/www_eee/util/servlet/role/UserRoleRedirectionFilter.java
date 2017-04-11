/*
 * Copyright 2007-2017 by Chris Hubick. All Rights Reserved.
 * 
 * This work is licensed under the terms of the "GNU AFFERO GENERAL PUBLIC LICENSE" version 3, as published by the Free
 * Software Foundation <http://www.gnu.org/licenses/>, a copy of which you should have received in the file LICENSE.txt.
 */

package net.www_eee.util.servlet.role;

import java.io.*;
import java.util.*;

import org.eclipse.jdt.annotation.*;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 * <p>
 * {@linkplain HttpServletResponse#sendRedirect(String) Redirects} users based on
 * {@linkplain HttpServletRequest#isUserInRole(String) roles}.
 * </p>
 * 
 * <p>
 * The name of each {@linkplain ServletConfig#getInitParameterNames() configuration parameter} (having the prefix '
 * <code>UserRoleRedirectionFilter.</code>' + &lt;{@link FilterConfig#getFilterName() FilterName}&gt; + ' <code>.</code>
 * ') represents a {@linkplain HttpServletRequest#isUserInRole(String) role}, and the value represents a
 * {@linkplain HttpServletResponse#sendRedirect(String) redirect location} to send users in that role. Users will be
 * sent to the location for the first {@linkplain String#equals(Object) matching} role.
 * </p>
 * 
 * @see HttpServletRequest#isUserInRole(String)
 * @see HttpServletResponse#sendRedirect(String)
 */
@NonNullByDefault
public class UserRoleRedirectionFilter implements Filter {
  /**
   * The name of the {@linkplain ServletConfig#getInitParameter(String) configuration parameter} (when prefixed with '
   * <code>UserRoleRedirectionFilter.</code>' + &lt;{@link FilterConfig#getFilterName() FilterName}&gt; + '
   * <code>.</code>') whose value specifies the location to {@linkplain HttpServletResponse#sendRedirect(String)
   * redirect} clients to if their {@linkplain HttpServletRequest#getRemoteUser() login} is <code>null</code>. If not
   * set, an {@linkplain HttpServletResponse#SC_UNAUTHORIZED unauthorized} response code will be
   * {@linkplain HttpServletResponse#sendError(int) sent}.
   */
  public static final String UNAUTHORIZED_LOCATION_PROP = "UnauthorizedLocation";
  /**
   * The name of the {@linkplain ServletConfig#getInitParameter(String) configuration parameter} (when prefixed with '
   * <code>UserRoleRedirectionFilter.</code>' + &lt;{@link FilterConfig#getFilterName() FilterName}&gt; + '
   * <code>.</code>') whose value specifies the location to {@linkplain HttpServletResponse#sendRedirect(String)
   * redirect} clients to if there is no location specified for their
   * {@linkplain HttpServletRequest#isUserInRole(String) role}. If not set, an
   * {@linkplain HttpServletResponse#SC_FORBIDDEN forbidden} response code will be
   * {@linkplain HttpServletResponse#sendError(int) sent}.
   */
  public static final String DEFAULT_LOCATION_PROP = "DefaultLocation";
  /**
   * @see #UNAUTHORIZED_LOCATION_PROP
   */
  protected @Nullable String unauthorizedLocation = null;
  /**
   * @see #DEFAULT_LOCATION_PROP
   */
  protected @Nullable String defaultLocation = null;
  /**
   * The locations to redirect roles to.
   */
  protected final Map<String,String> roleToLocationMappings = new HashMap<String,String>();

  @Override
  public void init(final FilterConfig filterConfig) {
    final String prefix = UserRoleRedirectionFilter.class.getSimpleName() + '.' + filterConfig.getFilterName() + '.';
    unauthorizedLocation = filterConfig.getInitParameter(prefix + UNAUTHORIZED_LOCATION_PROP);
    defaultLocation = filterConfig.getInitParameter(prefix + DEFAULT_LOCATION_PROP);
    Collections.list(filterConfig.getInitParameterNames()).stream().filter((name) -> name.startsWith(prefix)).filter((name) -> !name.equals(prefix + UNAUTHORIZED_LOCATION_PROP)).filter((name) -> !name.equals(prefix + DEFAULT_LOCATION_PROP)).forEach((name) -> roleToLocationMappings.put(name.substring(prefix.length()), Objects.requireNonNull(filterConfig.getInitParameter(name))));
    return;
  }

  @Override
  public void destroy() {
    unauthorizedLocation = null;
    defaultLocation = null;
    roleToLocationMappings.clear();
    return;
  }

  @Override
  public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws ServletException, IOException {
    final HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
    final HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;

    if ((httpServletRequest.getRemoteUser() == null) && (unauthorizedLocation != null)) {
      httpServletResponse.sendRedirect(unauthorizedLocation);
      return;
    }

    for (String role : roleToLocationMappings.keySet()) {
      if (httpServletRequest.isUserInRole(role)) {
        final String location = roleToLocationMappings.get(role);
        httpServletResponse.sendRedirect(location);
        return;
      }
    }

    if (defaultLocation != null) {
      httpServletResponse.sendRedirect(defaultLocation);
      return;
    }

    filterChain.doFilter(servletRequest, servletResponse);
    return;
  }

}
