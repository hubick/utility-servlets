/*
 * Copyright 2007-2017 by Chris Hubick. All Rights Reserved.
 * 
 * This work is licensed under the terms of the "GNU AFFERO GENERAL PUBLIC LICENSE" version 3, as published by the Free
 * Software Foundation <http://www.gnu.org/licenses/>, a copy of which you should have received in the file LICENSE.txt.
 */

package net.www_eee.util.servlet.role;

import org.eclipse.jdt.annotation.*;

import javax.servlet.http.*;


/**
 * Wrap the <code>request</code> to have {@link #isUserInRole(String) isUserInRole} return <code>true</code> for the
 * supplied <code>role</code>.
 * 
 * @see HttpServletRequest#isUserInRole(String)
 */
@NonNullByDefault
public class UserRoleRequestWrapper extends HttpServletRequestWrapper {
  /**
   * The role value to have {@link #isUserInRole(String) isUserInRole} return <code>true</code> for.
   */
  protected final String role;

  /**
   * Construct a <code>UserRoleRequestWrapper</code> around the given <code>request</code>.
   * 
   * @param request The {@link HttpServletRequest} to wrap.
   * @param role The role value to have {@link #isUserInRole(String) isUserInRole} return <code>true</code> for.
   */
  public UserRoleRequestWrapper(final HttpServletRequest request, final String role) {
    super(request);
    this.role = role;
    return;
  }

  @Override
  public boolean isUserInRole(final String role) {
    if (this.role.equals(role)) {
      return true;
    }
    return super.isUserInRole(role);
  }

}
