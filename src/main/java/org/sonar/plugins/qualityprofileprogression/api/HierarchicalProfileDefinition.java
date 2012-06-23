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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;

public abstract class HierarchicalProfileDefinition extends ProfileDefinition
{
	private static final String LOG_MESSAGE = "Creating profile '{}': {}";
	private static final String TRASH_PROFILE_NAME = "TrashHierarchicalProfileDefinition";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private QualityProfileProgressionHierarchy hierarchy;
	protected String analyserName;
	protected ProfileImporter importer;
	protected int setNumber;

	public HierarchicalProfileDefinition(ProfileImporter importer, QualityProfileProgressionHierarchy hierarchy, String analyserName, int setNumber)
	{
		this.importer = importer;
		this.hierarchy = hierarchy;
		this.analyserName = analyserName;
		this.setNumber = setNumber;
	}

	public RulesProfile createProfile(ValidationMessages messages)
	{
		String parentName = null;
		if (setNumber > 1)
		{
			parentName = hierarchy.getProfileName((setNumber - 1));
		}

		String rulesFilePath = hierarchy.getRulesFilePath(analyserName, setNumber);
		logger.debug("Importing rules file: {}", rulesFilePath);

		Reader rulesReader = null;
		RulesProfile profile = null;
		String languageKey = hierarchy.getLanguage().getKey();
		String profileName = hierarchy.getProfileName(setNumber);

		try
		{
			rulesReader = new InputStreamReader(getClass().getResourceAsStream(rulesFilePath));
			profile = this.importer.importProfile(rulesReader, messages);
			ValidationUtil.log(logger, messages, LOG_MESSAGE, profileName);

			profile.setLanguage(languageKey);

			if (messages.getErrors().size() == 0)
			{
				profile.setName(profileName);
			}
			else
			{
				profile.setName(TRASH_PROFILE_NAME);
			}

			if (parentName != null)
			{
				profile.setParentName(parentName);
			}
		}
		finally
		{
			try
			{
				rulesReader.close();
			}
			catch (IOException e1)
			{
				logger.error("Error closing InputStreamReader for file: " + rulesFilePath, e1);
			}
		}

		logger.debug("Profile settings: name={}, rules={}, enabled={}, provided={}, used={}, version={}",
				new Object[] { profile.getName(), profile.getActiveRules().size(), profile.getEnabled(), profile.getProvided(), profile.getUsed(),
						String.valueOf(profile.getVersion()) });

		return profile;
	}
}