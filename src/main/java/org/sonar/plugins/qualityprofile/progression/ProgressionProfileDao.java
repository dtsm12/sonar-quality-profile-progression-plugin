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
package org.sonar.plugins.qualityprofile.progression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.database.DatabaseSession;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.jpa.dao.ProfilesDao;

public class ProgressionProfileDao extends ProfilesDao
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public ProgressionProfileDao(DatabaseSession session)
	{
		super(session);
	}

	public List<RulesProfile> getRulesProfileByNamePattern(String languageKey, String namePattern)
	{
		logger.debug("getting {} RulesProfiles with name pattern '{}'", languageKey, namePattern);

		List<RulesProfile> profiles = getSession().getResults(RulesProfile.class, "language", Java.KEY);
		List<RulesProfile> matchProfiles = new ArrayList<RulesProfile>();
		Pattern pattern = Pattern.compile(namePattern);

		for (Iterator<RulesProfile> iterator = profiles.iterator(); iterator.hasNext();)
		{
			RulesProfile rulesProfile = iterator.next();

			if (pattern.matcher(rulesProfile.getName()).matches())
			{
				logger.debug("'{}' RulesProfile matches pattern", rulesProfile.getName());
				matchProfiles.add(rulesProfile);
			}
			else
			{
				logger.debug("'{}' RulesProfile does not match pattern", rulesProfile.getName());
			}
		}

		return matchProfiles;
	}
}
