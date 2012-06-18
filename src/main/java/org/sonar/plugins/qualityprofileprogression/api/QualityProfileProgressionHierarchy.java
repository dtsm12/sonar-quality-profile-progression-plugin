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
package org.sonar.plugins.qualityprofileprogression.api;

import org.sonar.api.ServerComponent;
import org.sonar.api.resources.Language;

public interface QualityProfileProgressionHierarchy extends ServerComponent
{
	public String getName();

	public Language getLanguage();

	public String getRulesFilePath(String analyserName, int setNumber);

	public String getProfileName(int setNumber);

	public String getParentProfileName(String profileName);

	public boolean isInHierarchy(String profileName);
}
