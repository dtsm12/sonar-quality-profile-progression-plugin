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
package org.sonar.plugins.qualityprofile.progression.server;

import org.junit.Test;
import org.sonar.api.notifications.Notification;
import org.sonar.plugins.emailnotifications.api.EmailMessage;
import org.sonar.plugins.qualityprofileprogression.ProfileProgressionPlugin;
import org.sonar.plugins.qualityprofileprogression.server.ProfileProgressedEmailTemplate;

import static org.hamcrest.text.StringContains.containsString;
import static org.junit.Assert.assertThat;

public class ProfileProgressedEmailTemplateTest
{
	@Test
	public void testProgressedNotification()
	{
		ProfileProgressedEmailTemplate template = new ProfileProgressedEmailTemplate();

		String projectKey = "testProjectKey";
		String projectName = "testProjectName";
		int percentage = 5;
		String version = "v1";
		String message = "test message";

		Notification notification = new Notification(ProfileProgressionPlugin.NOTIFICATION_TYPE_KEY);
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_NAME_KEY, projectName);
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_ID_KEY, "testProjectId");
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_KEY_KEY, projectKey);
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_VIOLATIONS_KEY, String.valueOf(percentage));
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_ANALYSIS_VERSION_KEY, version);
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_MESSAGE_KEY, message);
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_NOTIFICATION_USERS_KEY, "testUser");
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_PROGRESSED_KEY, "true");

		EmailMessage msg = template.format(notification);

		assertThat(msg.getSubject(), containsString(projectName));
		assertThat(msg.getSubject(), containsString("has been progressed"));
		assertThat(msg.getMessage(), containsString(projectKey));
		assertThat(msg.getMessage(), containsString("version " + version));
		assertThat(msg.getMessage(), containsString(percentage + "%"));
		assertThat(msg.getMessage(), containsString(message));
	}

	@Test
	public void testNotProgressedNotification()
	{
		ProfileProgressedEmailTemplate template = new ProfileProgressedEmailTemplate();

		String projectKey = "testProjectKey";
		String projectName = "testProjectName";
		int percentage = 5;
		String version = "v1";
		String message = "test message";

		Notification notification = new Notification(ProfileProgressionPlugin.NOTIFICATION_TYPE_KEY);
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_NAME_KEY, projectName);
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_ID_KEY, "testProjectId");
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_KEY_KEY, projectKey);
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_VIOLATIONS_KEY, String.valueOf(percentage));
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_ANALYSIS_VERSION_KEY, version);
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_MESSAGE_KEY, message);
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_NOTIFICATION_USERS_KEY, "testUser");
		notification.setFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_PROGRESSED_KEY, "false");

		EmailMessage msg = template.format(notification);

		assertThat(msg.getSubject(), containsString(projectName));
		assertThat(msg.getSubject(), containsString("has not been progressed"));
		assertThat(msg.getMessage(), containsString(projectKey));
		assertThat(msg.getMessage(), containsString("version " + version));
		assertThat(msg.getMessage(), containsString(percentage + "%"));
		assertThat(msg.getMessage(), containsString(message));
	}
}
