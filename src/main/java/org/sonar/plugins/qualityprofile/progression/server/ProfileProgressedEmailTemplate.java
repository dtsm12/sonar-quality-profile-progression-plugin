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
import org.sonar.plugins.emailnotifications.EmailConfiguration;
import org.sonar.plugins.emailnotifications.api.EmailMessage;
import org.sonar.plugins.emailnotifications.api.EmailTemplate;
import org.sonar.plugins.qualityprofile.progression.ProfileProgressionPlugin;

public class ProfileProgressedEmailTemplate extends EmailTemplate
{
	Logger logger = LoggerFactory.getLogger(this.getClass());

	String subjectTemplate1 = "%s quality profile has %sbeen progressed";
	String messageTemplate = "Analysis of %s, version %s, has reached %s%% violations.\n\n";

	private EmailConfiguration configuration;

	public ProfileProgressedEmailTemplate(/* EmailConfiguration configuration */)
	{
		// this.configuration = configuration;
	}

	@Override
	public EmailMessage format(Notification notification)
	{
		EmailMessage emailMessage = null;

		if (ProfileProgressionPlugin.NOTIFICATION_TYPE_KEY.equals(notification.getType()))
		{
			logger.debug("Preparing {} email message", ProfileProgressionPlugin.NOTIFICATION_TYPE_KEY);

			emailMessage = new EmailMessage();

			String projectName = notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_NAME_KEY);
			String projectId = notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_ID_KEY);
			String projectKey = notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_KEY_KEY);
			String percentageViolations = notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_VIOLATIONS_KEY);
			String version = notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_ANALYSIS_VERSION_KEY);
			String msg = notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_MESSAGE_KEY);
			boolean profileProgressed = Boolean.valueOf(notification.getFieldValue(ProfileProgressionPlugin.NOTIFICATION_PROJECT_PROGRESSED_KEY));

			StringBuilder sb = new StringBuilder();
			sb.append(String.format(messageTemplate, projectKey, version, percentageViolations));
			sb.append(msg);
			// appendFooter(sb, projectId);

			emailMessage.setMessageId(ProfileProgressionPlugin.NOTIFICATION_TYPE_KEY + "/" + projectId);
			emailMessage.setSubject(String.format(subjectTemplate1, projectName, (profileProgressed ? "" : "not ")));
			emailMessage.setMessage(sb.toString());

			logger.debug("Prepared {} email message", notification.getType());
			logger.debug("Message id: {}", emailMessage.getMessageId());
			logger.debug("Message subject: {}", emailMessage.getSubject());
			logger.debug("Message body: {}", emailMessage.getMessage());
		}

		return emailMessage;
	}

	private void appendFooter(StringBuilder sb, String projectId)
	{
		sb.append("\n").append("See it in Sonar: ").append(configuration.getServerBaseURL()).append("http://localhost:9000/dashboard/index/")
				.append(projectId).append("/\n");
	}
}
