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
package org.sonar.plugins.qualityprofileprogression.server;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.notifications.Notification;
import org.sonar.api.notifications.NotificationDispatcher;
import org.sonar.core.persistence.MyBatis;
import org.sonar.core.properties.PropertiesDao;
import org.sonar.plugins.qualityprofileprogression.ProfileProgressionPlugin;

// TODO write test class
public class ProfileProgressedNotificationDispatcher extends NotificationDispatcher
{
	/*private PropertiesDao propertiesDao;*/
	private Settings settings;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public ProfileProgressedNotificationDispatcher(Settings settings/*, MyBatis myBatis*/)
	{
		super();
		this.settings = settings;
		/*this.propertiesDao = new PropertiesDao(myBatis);*/
	}

	@Override
	public void dispatch(Notification notification, Context context)
	{
		try
		{
			if (StringUtils.equals(notification.getType(), ProfileProgressionPlugin.NOTIFICATION_TYPE_KEY))
			{
				logger.debug("Dispatching {}", ProfileProgressionPlugin.NOTIFICATION_TYPE_KEY);

				// add global setting users
				String[] globalUsers = settings.getStringArray(ProfileProgressionPlugin.GLOBAL_QUALITY_PROFILE_CHANGE_NOTIFICATION_USER_KEY);
				for (int i = 0; i < globalUsers.length; i++)
				{
					logger.debug("Adding global recipient: {}", globalUsers[i]);
					context.addUser(globalUsers[i]);
				}

				// add project setting users
				String projectUserString = notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_NOTIFICATION_USERS_KEY);
				String[] projectUsers = getStringArray(projectUserString);
				for (int i = 0; i < projectUsers.length; i++)
				{
					logger.debug("Adding project recipient: {}", projectUsers[i]);
					context.addUser(projectUsers[i]);
				}


				// add user's who have this project as one of their favourites - unable to get this working; just hangs
/*				Integer projectId = Integer.parseInt(notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_ID_KEY));
				List<String> userLogins = propertiesDao.findUserIdsForFavouriteResource(projectId);
				for (String userLogin : userLogins)
				{
					logger.debug("Adding favourite recipient: {}", userLogin);
					context.addUser(userLogin);
				}*/

				logger.debug("Dispatched {}", ProfileProgressionPlugin.NOTIFICATION_TYPE_KEY);
			}
		}
		catch (RuntimeException e)
		{
			logger.error("Error dispatching " + notification.getType(), e);
			throw e;
		}
	}

	public final String[] getStringArray(String value)
	{
		String separator = ",";

		if (value != null)
		{
			String[] strings = StringUtils.splitByWholeSeparator(value, separator);
			String[] result = new String[strings.length];
			for (int index = 0; index < strings.length; index++)
			{
				result[index] = StringUtils.trim(strings[index]);
			}
			return result;
		}
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}
}
