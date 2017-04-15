/*
 * Copyright 2012-2017 by Chris Hubick. All Rights Reserved.
 * 
 * This work is licensed under the terms of the "GNU AFFERO GENERAL PUBLIC LICENSE" version 3, as published by the Free
 * Software Foundation <http://www.gnu.org/licenses/>, a copy of which you should have received in the file LICENSE.txt.
 */

package net.www_eee.util.servlets.role;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.eclipse.jdt.annotation.*;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 * <p>
 * Populate clients into {@linkplain HttpServletRequest#isUserInRole(String) roles} based on their value for a specified
 * {@linkplain HttpServletRequest#getHeader(String) request header}.
 * </p>
 * 
 * <p>
 * The name of each {@linkplain ServletConfig#getInitParameterNames() configuration parameter} (prefixed with '
 * <code>RequestHeaderUserRoleFilter.</code>' + &lt;{@link FilterConfig#getFilterName() FilterName}&gt; + '
 * <code>.</code>') is {@linkplain Pattern#compile(String) parsed} as a {@link Pattern}, against which the
 * {@linkplain #HEADER_NAME_PROP specified} client {@linkplain HttpServletRequest#getHeader(String) request header} will
 * be {@linkplain Pattern#matcher(CharSequence) matched}, and if a {@linkplain Matcher#matches() match} is found, then
 * that client will be {@linkplain UserRoleRequestWrapper populated} into the
 * {@linkplain HttpServletRequest#isUserInRole(String) role} named by that configuration parameters value. Each client
 * may be placed into <em>multiple</em> matching roles.
 * </p>
 * 
 * <p>
 * This filter is useful, for instance, for using regular expressions against the <code>User-Agent</code> header in
 * order to place clients using certain types of devices into a special role which can be used to conditionalize content
 * or perform {@linkplain UserRoleRedirectionFilter redirects}.
 * </p>
 * 
 * @see HttpServletRequest#isUserInRole(String)
 */
@NonNullByDefault
public class RequestHeaderUserRoleFilter implements Filter {
  /**
   * The name of the {@linkplain FilterConfig#getInitParameter(String) configuration parameter} (when prefixed with '
   * <code>RequestHeaderUserRoleFilter.</code>' + &lt;{@link FilterConfig#getFilterName() FilterName}&gt; + '
   * <code>.</code>') whose <code>String</code> value specifies the {@linkplain HttpServletRequest#getHeader(String)
   * request header} this filter will examine.
   */
  public static final String HEADER_NAME_PROP = "HEADER_NAME";
  /**
   * The name of the {@linkplain FilterConfig#getInitParameter(String) configuration parameter} (when prefixed with '
   * <code>RequestHeaderUserRoleFilter.</code>' + &lt;{@link FilterConfig#getFilterName() FilterName}&gt; + '
   * <code>.</code>') whose <code>Boolean</code> value specifies if the value for the {@linkplain #HEADER_NAME_PROP
   * request header} being examined by this filter should first be made {@linkplain String#toLowerCase() lower-case}
   * before comparison?
   */
  public static final String LOWER_CASE_VALUE_PROP = "LOWER_CASE_VALUE";
  /**
   * @see #HEADER_NAME_PROP
   */
  protected @Nullable String headerName;
  /**
   * @see #LOWER_CASE_VALUE_PROP
   */
  protected boolean lowerCaseValue = true;
  /**
   * The {@link Pattern}'s to {@linkplain Pattern#matcher(CharSequence) match} the {@linkplain #HEADER_NAME_PROP
   * specified} {@linkplain HttpServletRequest#getHeader(String) header} against, mapped to a <code>String</code> naming
   * the {@linkplain HttpServletRequest#isUserInRole(String) role} a {@linkplain Matcher#matches() matching} client
   * should be {@linkplain UserRoleRequestWrapper populated} into.
   */
  protected final Map<Pattern,String> regexpToRoleMappings = new HashMap<Pattern,String>();

  @Override
  public void init(final FilterConfig filterConfig) {
    final String prefix = RequestHeaderUserRoleFilter.class.getSimpleName() + '.' + filterConfig.getFilterName() + '.';
    headerName = filterConfig.getInitParameter(prefix + HEADER_NAME_PROP);
    final String lowerCaseValueProp = filterConfig.getInitParameter(prefix + LOWER_CASE_VALUE_PROP);
    lowerCaseValue = (lowerCaseValueProp == null) || (Boolean.valueOf(lowerCaseValueProp).booleanValue());
    Collections.list(filterConfig.getInitParameterNames()).stream().filter((name) -> name.startsWith(prefix)).filter((name) -> !name.equals(prefix + HEADER_NAME_PROP)).filter((name) -> !name.equals(prefix + LOWER_CASE_VALUE_PROP)).forEach((name) -> regexpToRoleMappings.put(Pattern.compile(name.substring(prefix.length())), Objects.requireNonNull(filterConfig.getInitParameter(name)).intern()));
    return;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws ServletException, IOException {
    String headerValue = ((HttpServletRequest)servletRequest).getHeader(headerName);
    if (headerValue == null) headerValue = "";
    if (lowerCaseValue) headerValue = headerValue.toLowerCase();

    for (Map.Entry<Pattern,String> regexp : regexpToRoleMappings.entrySet()) {
      if (regexp.getKey().matcher(headerValue).matches()) {
        servletRequest = new UserRoleRequestWrapper((HttpServletRequest)servletRequest, regexp.getValue());
      }
    }

    filterChain.doFilter(servletRequest, servletResponse);
    return;
  }

  @Override
  public void destroy() {
    return;
  }

}
