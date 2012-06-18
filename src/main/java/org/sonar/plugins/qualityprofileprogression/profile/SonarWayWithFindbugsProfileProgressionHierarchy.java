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
package org.sonar.plugins.qualityprofileprogression.profile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Language;
import org.sonar.plugins.qualityprofileprogression.api.QualityProfileProgressionHierarchy;

public class SonarWayWithFindbugsProfileProgressionHierarchy implements QualityProfileProgressionHierarchy
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String RESOURCE_PATH = "/org/sonar/plugins/qualityprofileprogression/profile/";

	public static final String SET_NUMBER_PATTERN = "([\\d]+)";
	public static final String PROFILE_NAME_PATTERN = "Sonar way with Findbugs \\(Set" + SET_NUMBER_PATTERN + "\\)";
	Pattern pattern = Pattern.compile(SonarWayWithFindbugsProfileProgressionHierarchy.PROFILE_NAME_PATTERN);

	public String getName()
	{
		return "Sonar way with Findbugs Huierarchy";
	}

	public Language getLanguage()
	{
		return Java.INSTANCE;
	}

	public String getRulesFilePath(String analyserName, int setNumber)
	{
		String setName = "Set" + setNumber;
		String rulesFileName = analyserName + "-" + setName;

		return RESOURCE_PATH + rulesFileName + ".xml";
	}

	public boolean isInHierarchy(String profileName)
	{
		return pattern.matcher(profileName).matches();
	}

	public String getProfileName(int setNumber)
	{
		return SonarWayWithFindbugsProfileProgressionHierarchy.PROFILE_NAME_PATTERN.replace(
				SonarWayWithFindbugsProfileProgressionHierarchy.SET_NUMBER_PATTERN, String.valueOf(setNumber)).replace("\\", "");
	}

	public String getParentProfileName(String profileName)
	{
		// Initialise variables & regex pattern
		String parentName = null;
		String setNumber = null;
		Matcher matcher = pattern.matcher(profileName);

		// get Profile set's group number
		if (matcher.matches())
		{
			setNumber = matcher.group(1);
		}

		// try to get previous set number
		int setNo = 0;
		try
		{
			setNo = Integer.parseInt(setNumber);
		}
		catch (NumberFormatException e)
		{
			logger.error("Unable to get set number from profile name '" + profileName + "'", e);
		}

		// if it's not the first set it has a parent
		if (setNo > 1)
		{
			int parentSetNo = setNo - 1;
			parentName = getProfileName(parentSetNo);
		}

		return parentName;
	}
}
