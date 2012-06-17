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

import java.io.InputStreamReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.qualityprofile.progression.ValidationUtil;

public class SonarWayWithFindbugsSubSetProfile extends ProfileDefinition
{
	private static final String LOG_MESSAGE = "Creating profile '{}': {}";

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private SonarWayWithFindbugsSubSetName profileNameUtil = new SonarWayWithFindbugsSubSetName();

	private String analyserName;
	private ProfileImporter importer;
	private int setNumber;

	public SonarWayWithFindbugsSubSetProfile(ProfileImporter importer, String analyserName, int setNumber)
	{
		this.importer = importer;
		this.analyserName = analyserName;
		this.setNumber = setNumber;
	}

	public RulesProfile createProfile(ValidationMessages messages)
	{
		String setName = "Set" + setNumber;
		String rulesFileName = analyserName + "-" + setName;
		String parentName = null;
		if (setNumber > 1)
		{
			parentName = profileNameUtil.getProfileName((setNumber - 1));
		}

		String rulesFilePath = "/org/sonar/plugins/qualityprofile/progression/profile/" + rulesFileName + ".xml";

		logger.debug("Importing rules file: {}", rulesFilePath);

		Reader rulesReader = new InputStreamReader(getClass().getResourceAsStream(rulesFilePath));

		RulesProfile profile = this.importer.importProfile(rulesReader, messages);
		ValidationUtil.log(logger, messages, LOG_MESSAGE, profile.getName());
		profile.setLanguage(Java.KEY);
		profile.setName(profileNameUtil.getProfileName(setNumber));

		if (parentName != null)
		{
			logger.debug("Setting parent profile for '{}' to '{}'", profile.getName(), parentName);
			profile.setParentName(parentName);
		}

		return profile;
	}
}
