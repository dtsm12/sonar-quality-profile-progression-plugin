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
package org.sonar.plugins.qualityprofile.progression.profile;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.database.DatabaseSession;
import org.sonar.api.platform.Server;
import org.sonar.api.platform.ServerStartHandler;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.jpa.dao.RulesDao;
import org.sonar.plugins.qualityprofile.progression.ProgressionProfileDao;
import org.sonar.plugins.qualityprofile.progression.ValidationUtil;

public class ProfileRulesInheritanceUpdater implements ServerStartHandler
{
	private static final String LOG_MESSAGE = "changeParentProfile for '{}': {}";

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private SonarWayWithFindbugsSubSetName profileNameUtil = new SonarWayWithFindbugsSubSetName();

	private ProgressionProfileDao profilesDao;
	private RulesDao rulesDao;
	private ProfilesManager profilesManager;

	public ProfileRulesInheritanceUpdater(DatabaseSession session)
	{
		super();
		this.profilesDao = new ProgressionProfileDao(session);
		this.rulesDao = new RulesDao(session);
		this.profilesManager = new ProfilesManager(session, rulesDao);
	}

	public void onServerStart(Server server)
	{
		try
		{
			logger.debug("Building progression profile hierarchy");

			List<RulesProfile> rulesProfiles = profilesDao.getRulesProfileByNamePattern(Java.KEY,
					SonarWayWithFindbugsSubSetName.PROFILE_NAME_PATTERN);

			for (Iterator<RulesProfile> iterator = rulesProfiles.iterator(); iterator.hasNext();)
			{
				RulesProfile rulesProfile = (RulesProfile) iterator.next();
				String parentName = profileNameUtil.getParentProfileName(rulesProfile.getName());
				if (StringUtils.isNotEmpty(parentName))
				{
					logger.debug("Updating rules inheritance for profile {} from profile parent {}", rulesProfile.getName(), parentName);
					ValidationMessages messages = profilesManager.changeParentProfile(rulesProfile.getId(), parentName, "admin");
					ValidationUtil.log(logger, messages, LOG_MESSAGE, rulesProfile.getName());
				}
				else
				{
					logger.debug("Parent profile for {} is null. Rules inheritance not updated.", rulesProfile.getName());
				}
			}

			logger.debug("Progression profile hierarchy built.");
		}
		catch (RuntimeException e)
		{
			logger.error("Error building progression profile hierarchy", e);
			throw e;
		}
	}
}
