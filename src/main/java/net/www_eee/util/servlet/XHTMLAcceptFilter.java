/*
 * Copyright 2007-2017 by Chris Hubick. All Rights Reserved.
 * 
 * This work is licensed under the terms of the "GNU AFFERO GENERAL PUBLIC LICENSE" version 3, as published by the Free
 * Software Foundation <http://www.gnu.org/licenses/>, a copy of which you should have received in the file LICENSE.txt.
 */

package net.www_eee.util.servlet;

import java.io.*;
import java.util.*;

import javax.activation.*;

import org.eclipse.jdt.annotation.*;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 * A filter to only {@linkplain ServletResponse#setContentType(String) send} an XHTML content type for clients which can
 * accept it, else send the content using the "text/html" content type.
 * 
 * @see ServletResponse#setContentType(String)
 */
@NonNullByDefault
public class XHTMLAcceptFilter implements Filter {

  @Override
  public void init(final FilterConfig filterConfig) {
    return;
  }

  /**
   * Is the client behind the <code>request</code> capable of accepting an XHTML response?
   * 
   * @param request The {@link HttpServletRequest} by the client in question.
   * @return <code>true</code> if the <code>request</code> contains an <code>Accept</code>
   * {@linkplain HttpServletRequest#getHeaders(String) header} indicating the client is capable of receiving XHTML
   * content.
   * @throws IllegalArgumentException If the supplied <code>request</code> is <code>null</code>.
   */
  public static final boolean acceptsXHTML(final HttpServletRequest request) throws IllegalArgumentException {
    final Enumeration<String> values = request.getHeaders("Accept");
    while (values.hasMoreElements()) {
      final String acceptHeaderLC = values.nextElement().toLowerCase();
      if (acceptHeaderLC.contains("application/xhtml+xml")) return true;
      if (acceptHeaderLC.contains("text/xml")) return true;
      if (acceptHeaderLC.contains("application/xml")) return true;
    }
    return false;
  }

  @Override
  public void doFilter(final ServletRequest servletRequest, ServletResponse servletResponse, final FilterChain filterChain) throws ServletException, IOException {
    if (!acceptsXHTML((HttpServletRequest)servletRequest)) servletResponse = new HTMLResponseWrapper((HttpServletResponse)servletResponse);
    filterChain.doFilter(servletRequest, servletResponse);
    return;
  }

  @Override
  public void destroy() {
    return;
  }

  /**
   * Wraps an {@link HttpServletResponse} to intercept calls to {@link #setContentType(String)}, changing
   * "application/xhtml+xml" types to "text/html" ones.
   */
  protected static class HTMLResponseWrapper extends HttpServletResponseWrapper {

    /**
     * Construct a <code>HTMLResponseWrapper</code>.
     * 
     * @param response The response being wrapped.
     */
    public HTMLResponseWrapper(final HttpServletResponse response) {
      super(response);
      return;
    }

    /**
     * Convert any "application/xhtml+xml" types to "text/html".
     * 
     * @param type The value being examined.
     * @return The fixed type.
     */
    protected static final @Nullable String fixType(final @Nullable String type) {
      if ((type == null) || (type.isEmpty())) return type;
      final MimeType mimeType;
      try {
        mimeType = new MimeType(type);
      } catch (MimeTypeParseException mtpe) {
        return type;
      }
      if (!"application/xhtml+xml".equalsIgnoreCase(mimeType.getBaseType())) {
        return type;
      }
      final MimeTypeParameterList parameters = mimeType.getParameters();
      return "text/html" + ((parameters != null) ? parameters.toString() : "");
    }

    @Override
    public void setHeader(final String name, @Nullable String value) {
      if ("Content-Type".equalsIgnoreCase(name)) value = fixType(value);
      super.setHeader(name, value);
      return;
    }

    @Override
    public void addHeader(final String name, @Nullable String value) {
      if ("Content-Type".equalsIgnoreCase(name)) value = fixType(value);
      super.addHeader(name, value);
      return;
    }

    @Override
    public void setContentType(final @Nullable String type) {
      super.setContentType(fixType(type));
      return;
    }

  } // HTMLResponseWrapper

}
