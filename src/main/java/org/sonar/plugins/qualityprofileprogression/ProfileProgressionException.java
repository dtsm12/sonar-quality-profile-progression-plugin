/*
 * Quality Profile Progression
 * Copyright (C) 2012 David T S Maitland
 * david.ts.maitland@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.qualityprofileprogression;

public class ProfileProgressionException extends Exception
{
  static final long serialVersionUID = -7740484489848775578L;

  public ProfileProgressionException()
  {
    super();
  }

  public ProfileProgressionException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ProfileProgressionException(String message)
  {
    super(message);
  }

  public ProfileProgressionException(Throwable cause)
  {
    super(cause);
  }

}
