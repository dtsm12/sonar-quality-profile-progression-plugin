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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.notifications.Notification;
import org.sonar.plugins.emailnotifications.api.EmailMessage;
import org.sonar.plugins.emailnotifications.api.EmailTemplate;
import org.sonar.plugins.qualityprofile.progression.ProfileProgressionPlugin;

public class ProfileProgressedEmailTemplate extends EmailTemplate
{
	Logger logger = LoggerFactory.getLogger(this.getClass());

	String subjectTemplate1 = "%1$s quality profile has been progressed";
	String messageTemplate = "Analysis of %1$s, version %2$s, has reached %3$s'%' violations which is under the quality profile progression threshold (%4$s'%').\n";

	@Override
	public EmailMessage format(Notification notification)
	{
		if (ProfileProgressionPlugin.NOTIFICATION_TYPE_KEY.equals(notification.getType()) == false)
		{
			return null;
		}

		logger.debug("Preparing {} email message", ProfileProgressionPlugin.NOTIFICATION_TYPE_KEY);

		EmailMessage emailMessage = new EmailMessage();

		String projectName = notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_NAME_KEY);
		String projectId = notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_ID_KEY);
		String projectKey = notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_KEY_KEY);
		String percentageViolations = notification
				.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_VIOLATIONS_KEY);
		String version = notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_ANALYSIS_VERSION_KEY);
		String violationThreshold = notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_VIOLAIONS_THRESHOLD_KEY);
		String msg = notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_MESSAGE_KEY);

		emailMessage.setMessageId(ProfileProgressionPlugin.NOTIFICATION_TYPE_KEY + "/" + projectId);
		emailMessage.setSubject(String.format(subjectTemplate1, projectName));
		emailMessage.setMessage(String.format(messageTemplate, projectKey, version, percentageViolations, violationThreshold) + msg);

		logger.debug("Prepared {} email message", ProfileProgressionPlugin.NOTIFICATION_TYPE_KEY);
		logger.debug("Message id: {}", emailMessage.getMessageId());
		logger.debug("Message subject: {}", emailMessage.getSubject());
		logger.debug("Message body: {}", emailMessage.getMessage());

		return emailMessage;
	}
}
