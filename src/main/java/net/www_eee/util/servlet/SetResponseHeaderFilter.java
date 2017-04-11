/*
 * Copyright 2007-2017 by Chris Hubick. All Rights Reserved.
 * 
 * This work is licensed under the terms of the "GNU AFFERO GENERAL PUBLIC LICENSE" version 3, as published by the Free
 * Software Foundation <http://www.gnu.org/licenses/>, a copy of which you should have received in the file LICENSE.txt.
 */

package net.www_eee.util.servlet;

import java.io.*;
import java.util.*;

import org.eclipse.jdt.annotation.*;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 * <p>
 * Set an HTTP response header for each configured {@linkplain FilterConfig#getInitParameter(String) init parameter}.
 * </p>
 * 
 * <p>
 * Place something like the following in your web.xml, as a child of the web-app element, after the display-name and
 * description elements, but before any servlet elements:
 * </p>
 * 
 * <pre>
 * &lt;filter&gt;
 *   &lt;filter-name&gt;ResourceCacheControl&lt;/filter-name&gt;
 *   &lt;filter-class&gt;net.www_eee.util.misc.servlet.SetResponseHeaderFilter&lt;/filter-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;SetResponseHeaderFilter.ResourceCacheControl.Cache-Control&lt;/param-name&gt;
 *     &lt;param-value&gt;max-age=3600, public&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 * &lt;/filter&gt;
 * &lt;filter-mapping&gt;
 *   &lt;filter-name&gt;ResourceCacheControl&lt;/filter-name&gt;
 *   &lt;url-pattern&gt;*.png&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 */
@NonNullByDefault
public class SetResponseHeaderFilter implements Filter {
  /**
   * The key to a boolean config option which will cause the headers to be
   * {@linkplain HttpServletResponse#addHeader(String, String) added} instead of
   * {@linkplain HttpServletResponse#setHeader(String, String) set} (default behavior).
   */
  public static final String ADDITIVE_MODE_ENABLE_PROP = "AdditiveMode.Enable";
  /**
   * The key to a boolean config option which will cause the headers to be set/added <em>after</em> servlet processing.
   */
  public static final String POST_MODE_ENABLE_PROP = "PostMode.Enable";
  /**
   * The configuration supplied to this filter instance.
   */
  protected Map<String,String> headers = new HashMap<String,String>();
  /**
   * @see #ADDITIVE_MODE_ENABLE_PROP
   */
  protected boolean additiveMode = false;
  /**
   * @see #POST_MODE_ENABLE_PROP
   */
  protected boolean postMode = false;

  @Override
  public void init(final FilterConfig filterConfig) {
    final String prefix = SetResponseHeaderFilter.class.getSimpleName() + '.' + filterConfig.getFilterName() + '.';
    additiveMode = Boolean.parseBoolean(filterConfig.getInitParameter(prefix + ADDITIVE_MODE_ENABLE_PROP));
    postMode = Boolean.parseBoolean(filterConfig.getInitParameter(prefix + POST_MODE_ENABLE_PROP));
    Collections.list(filterConfig.getInitParameterNames()).stream().filter((name) -> name.startsWith(prefix)).filter((name) -> !name.equals(prefix + ADDITIVE_MODE_ENABLE_PROP)).filter((name) -> !name.equals(prefix + POST_MODE_ENABLE_PROP)).forEach((name) -> headers.put(name.substring(prefix.length()), Objects.requireNonNull(filterConfig.getInitParameter(name))));
    return;
  }

  @Override
  public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws ServletException, IOException {

    if (postMode) {
      filterChain.doFilter(servletRequest, servletResponse);
    }

    final HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;
    for (Map.Entry<String,String> header : headers.entrySet()) {
      if (additiveMode) {
        httpServletResponse.addHeader(header.getKey(), header.getValue());
      } else {
        httpServletResponse.setHeader(header.getKey(), header.getValue());
      }
    }

    if (!postMode) {
      filterChain.doFilter(servletRequest, servletResponse);
    }

    return;
  }

  @Override
  public void destroy() {
    headers.clear();
    return;
  }

}
