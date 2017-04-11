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
 * Populate clients into {@linkplain HttpServletRequest#isUserInRole(String) roles} based on their
 * {@linkplain ServletRequest#getLocales() locales}.
 * 
 * @see HttpServletRequest#isUserInRole(String)
 * @see ServletRequest#getLocales()
 */
@NonNullByDefault
public class LocaleUserRoleFilter implements Filter {
  /**
   * @see #ROLE_PREFIX_PROP
   */
  protected static final String ROLE_PREFIX_DEFAULT = "locale-";
  /**
   * The name of the {@linkplain FilterConfig#getInitParameter(String) configuration parameter} whose value will prefix
   * the created roles.
   */
  public static final String ROLE_PREFIX_PROP = LocaleUserRoleFilter.class.getSimpleName() + ".Prefix";
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
  public void doFilter(ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws ServletException, IOException {
    final HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
    if (httpServletRequest.getHeader("Accept-Language") != null) {
      servletRequest = new LocaleUserRoleRequestWrapper(httpServletRequest);
    }
    filterChain.doFilter(servletRequest, servletResponse);
    return;
  }

  @Override
  public void destroy() {
    return;
  }

  /**
   * Wrap the <code>request</code> and populate clients into {@linkplain HttpServletRequest#isUserInRole(String) roles}
   * based on their {@linkplain ServletRequest#getLocales() locales}.
   */
  protected class LocaleUserRoleRequestWrapper extends HttpServletRequestWrapper {

    /**
     * Construct a <code>LocaleUserRoleRequestWrapper</code>.
     * 
     * @param request The {@linkplain HttpServletRequest} to wrap.
     */
    public LocaleUserRoleRequestWrapper(final HttpServletRequest request) {
      super(request);
      return;
    }

    @Override
    public boolean isUserInRole(final String role) {
      if (role.startsWith(rolePrefix)) {
        final Locale roleLocale;
        try {
          roleLocale = Locale.forLanguageTag(role.substring(rolePrefix.length()));
        } catch (Exception e) {
          return super.isUserInRole(role);
        }
        final Enumeration<Locale> requestLocales = getLocales();
        while (requestLocales.hasMoreElements()) {
          final Locale requestLocale = requestLocales.nextElement();
          if ((roleLocale.getLanguage().equals(requestLocale.getLanguage())) && ((roleLocale.getCountry().isEmpty()) || (roleLocale.getCountry().equals(requestLocale.getCountry())))) return true;
        }
      }
      return super.isUserInRole(role);
    }

  } // LocaleUserRoleRequestWrapper

}
