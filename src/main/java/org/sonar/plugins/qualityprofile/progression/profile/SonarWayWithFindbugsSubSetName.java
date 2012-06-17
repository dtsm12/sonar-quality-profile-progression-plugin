package org.sonar.plugins.qualityprofile.progression.profile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SonarWayWithFindbugsSubSetName
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String SET_NUMBER_PATTERN = "([\\d]+)";
	public static final String PROFILE_NAME_PATTERN = "Sonar way with Findbugs \\(Set" + SET_NUMBER_PATTERN + "\\)";
	Pattern pattern = Pattern.compile(SonarWayWithFindbugsSubSetName.PROFILE_NAME_PATTERN);

	public String getProfileName(int setNumber)
	{
		return SonarWayWithFindbugsSubSetName.PROFILE_NAME_PATTERN.replace(SonarWayWithFindbugsSubSetName.SET_NUMBER_PATTERN,
				String.valueOf(setNumber)).replace("\\", "");
	}

	protected String getParentProfileName(String profileName)
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
