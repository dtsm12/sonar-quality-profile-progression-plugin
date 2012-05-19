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
package org.sonar.jpa.dao;

import org.sonar.api.database.DatabaseSession;
import org.sonar.api.profiles.RulesProfile;

public class MockProfilesDao extends ProfilesDao
{

	public MockProfilesDao(DatabaseSession session)
	{
		super(session);
	}

	public RulesProfile getProfile(String languageKey, String profileName)
	{
		return getSession().getEntityManager().find(RulesProfile.class, languageKey + profileName);
	}
}
