/*
 * Copyright 2007-2014 by Chris Hubick. All Rights Reserved.
 * 
 * This work is licensed under the terms of the "GNU AFFERO GENERAL PUBLIC LICENSE" version 3, as published by the Free
 * Software Foundation <http://www.gnu.org/licenses/>, a copy of which you should have received in the file LICENSE.txt.
 */

package net.www_eee.util.servlet.session;

import java.io.*;

import org.eclipse.jdt.annotation.*;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 * A servlet to {@linkplain HttpSession#invalidate() invalidate} the client session and then do a
 * {@linkplain HttpServletResponse#sendRedirect(String) redirect}.
 * 
 * @see HttpSession#invalidate()
 * @see HttpServletResponse#sendRedirect(String)
 */
@NonNullByDefault
public class SessionInvalidator extends HttpServlet {
  /**
   * The name of the {@linkplain ServletConfig#getInitParameter(String) configuration parameter} (when prefixed with '
   * <code>SessionInvalidator.</code>' + &lt;{@link ServletConfig#getServletName() ServletName}&gt; + ' <code>.</code>')
   * whose value specifies the location to {@linkplain HttpServletResponse#sendRedirect(String) redirect} clients to
   * after their session has been {@linkplain HttpSession#invalidate() invalidated}. If not set, an
   * {@linkplain HttpServletResponse#SC_NO_CONTENT no-content} response code will be
   * {@linkplain HttpServletResponse#sendError(int) sent}.
   */
  public static final String REDIRECT_LOCATION_PROP = "RedirectLocation";
  /**
   * @see #REDIRECT_LOCATION_PROP
   */
  protected @Nullable String redirectLocation = null;

  @Override
  public void init() throws ServletException {
    final String prefix = SessionInvalidator.class.getSimpleName() + '.' + getServletConfig().getServletName() + '.';
    redirectLocation = getServletConfig().getInitParameter(prefix + REDIRECT_LOCATION_PROP);
    return;
  }

  @Override
  public void destroy() {
    redirectLocation = null;
    return;
  }

  @Override
  public void doGet(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) throws ServletException, IOException {
    final HttpSession session = servletRequest.getSession(false);
    if (session != null) {
      try {
        session.invalidate();
      } catch (Exception e) {}
    }

    final Cookie sessionCookie = new Cookie("JSESSIONID", "invalidated");
    sessionCookie.setMaxAge(0);
    if (!servletRequest.getContextPath().isEmpty()) {
      sessionCookie.setPath(servletRequest.getContextPath());
    } else {
      sessionCookie.setPath("/");
    }
    if (servletRequest.isSecure()) sessionCookie.setSecure(true);
    servletResponse.addCookie(sessionCookie);

    if (redirectLocation != null) {
      servletResponse.sendRedirect(redirectLocation);
    } else {
      servletResponse.sendError(HttpServletResponse.SC_NO_CONTENT);
    }
    return;
  }

}
