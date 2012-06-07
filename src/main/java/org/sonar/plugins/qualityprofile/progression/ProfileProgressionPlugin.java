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

import org.sonar.plugins.qualityprofile.progression.batch.ProfileProgressionDecorator;
import org.sonar.plugins.qualityprofile.progression.server.ProfileProgressedEmailTemplate;
import org.sonar.plugins.qualityprofile.progression.server.ProfileProgressedNotificationDispatcher;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

@Properties({
		@Property(key = ProfileProgressionPlugin.QUALITY_PROFILE_CHANGE_THRESHOLD_KEY, name = ProfileProgressionPlugin.QUALITY_PROFILE_CHANGE_THRESHOLD_NAME, description = ProfileProgressionPlugin.QUALITY_PROFILE_CHANGE_THRESHOLD_DESC, defaultValue = "10", project = true),
		@Property(key = ProfileProgressionPlugin.GLOBAL_QUALITY_PROFILE_CHANGE_ENABLED_KEY, name = ProfileProgressionPlugin.QUALITY_PROFILE_CHANGE_ENABLED_NAME, description = ProfileProgressionPlugin.QUALITY_PROFILE_CHANGE_ENABLED_DESC, defaultValue = "false"),
		@Property(key = ProfileProgressionPlugin.GLOBAL_TARGET_LANGUAGE_QUALITY_PROFILE_KEY, name = ProfileProgressionPlugin.GLOBAL_TARGET_LANGUAGE_QUALITY_PROFILE_NAME, description = ProfileProgressionPlugin.GLOBAL_TARGET_LANGUAGE_QUALITY_PROFILE_DESC),
		@Property(key = ProfileProgressionPlugin.GLOBAL_QUALITY_PROFILE_CHANGE_NOTIFICATION_USER_KEY, name = ProfileProgressionPlugin.QUALITY_PROFILE_CHANGE_NOTIFICATION_USER_NAME, description = ProfileProgressionPlugin.QUALITY_PROFILE_CHANGE_NOTIFICATION_USER_DESC),
		@Property(key = ProfileProgressionPlugin.PROJECT_QUALITY_PROFILE_CHANGE_ENABLED_KEY, name = ProfileProgressionPlugin.QUALITY_PROFILE_CHANGE_ENABLED_NAME, description = ProfileProgressionPlugin.QUALITY_PROFILE_CHANGE_ENABLED_DESC, defaultValue = "true", project = true, global = false),
		@Property(key = ProfileProgressionPlugin.PROJECT_TARGET_LANGUAGE_QUALITY_PROFILE_KEY, name = ProfileProgressionPlugin.PROJECT_TARGET_LANGUAGE_QUALITY_PROFILE_NAME, description = ProfileProgressionPlugin.PROJECT_TARGET_LANGUAGE_QUALITY_PROFILE_DESC, project = true, global = false),
		@Property(key = ProfileProgressionPlugin.PROJECT_QUALITY_PROFILE_CHANGE_NOTIFICATION_USER_KEY, name = ProfileProgressionPlugin.QUALITY_PROFILE_CHANGE_NOTIFICATION_USER_NAME, description = ProfileProgressionPlugin.QUALITY_PROFILE_CHANGE_NOTIFICATION_USER_DESC, project = true, global = false) })
public class ProfileProgressionPlugin extends SonarPlugin
{
	// Settings' constants
	public static final String QUALITY_PROFILE_CHANGE_ENABLED_NAME = "Enabled";
	public static final String GLOBAL_QUALITY_PROFILE_CHANGE_ENABLED_KEY = "org.sonar.plugins.qualityprofile.progression.progression.enabled";
	public static final String PROJECT_QUALITY_PROFILE_CHANGE_ENABLED_KEY = "org.sonar.plugins.qualityprofile.progression.progression.project.enabled";
	public static final String QUALITY_PROFILE_CHANGE_ENABLED_DESC = "Whether quality profile progression is enabled.";

	public static final String TARGET_LANGUAGE_QUALITY_PROFILE_DELIM = ";";
	public static final String TARGET_LANGUAGE_QUALITY_PROFILE_ASSIGN = "=";

	public static final String GLOBAL_TARGET_LANGUAGE_QUALITY_PROFILE_NAME = "Target quality profile(s)";
	public static final String GLOBAL_TARGET_LANGUAGE_QUALITY_PROFILE_KEY = "org.sonar.plugins.qualityprofile.progression.language.targets";
	public static final String GLOBAL_TARGET_LANGUAGE_QUALITY_PROFILE_DESC = "The bottom quality profile in the hierarchy to progress down. Specify one per language. (e.g.  java"
			+ TARGET_LANGUAGE_QUALITY_PROFILE_ASSIGN
			+ "Sonar way"
			+ TARGET_LANGUAGE_QUALITY_PROFILE_DELIM
			+ "web"
			+ TARGET_LANGUAGE_QUALITY_PROFILE_ASSIGN + "JSF Profile" + TARGET_LANGUAGE_QUALITY_PROFILE_DELIM + ").";

	public static final String PROJECT_TARGET_LANGUAGE_QUALITY_PROFILE_NAME = "Target quality profile";
	public static final String PROJECT_TARGET_LANGUAGE_QUALITY_PROFILE_KEY = "org.sonar.plugins.qualityprofile.progression.project.target";
	public static final String PROJECT_TARGET_LANGUAGE_QUALITY_PROFILE_DESC = "The bottom quality profile in the hierarchy to progress down.";

	public static final String QUALITY_PROFILE_CHANGE_THRESHOLD_NAME = "Violation threshold";
	public static final String QUALITY_PROFILE_CHANGE_THRESHOLD_DESC = "The violation % under which the quality profile will be progressed.";
	public static final String QUALITY_PROFILE_CHANGE_THRESHOLD_KEY = "org.sonar.plugins.qualityprofile.progression.threshold";

	public static final String QUALITY_PROFILE_CHANGE_NOTIFICATION_USER_NAME = "Notification users";
	public static final String GLOBAL_QUALITY_PROFILE_CHANGE_NOTIFICATION_USER_KEY = "org.sonar.plugins.qualityprofile.progression.notification.user";
	public static final String PROJECT_QUALITY_PROFILE_CHANGE_NOTIFICATION_USER_KEY = "org.sonar.plugins.qualityprofile.progression.project.notification.user";
	public static final String QUALITY_PROFILE_CHANGE_NOTIFICATION_USER_DESC = "Sonar user logins who should recieve an email when project's quality profile is progressed (comma-separated).";

	// Notification constants
	public static final String NOTIFICATION_TYPE_KEY = "org.sonar.plugins.qualityprofile.progression.notification";
	public static final String NOTIFICATION_PROJECT_NAME_KEY = "org.sonar.plugins.qualityprofile.progression.notification.project.name";
	public static final String NOTIFICATION_PROJECT_ID_KEY = "org.sonar.plugins.qualityprofile.progression.notification.project.id";
	public static final String NOTIFICATION_PROJECT_KEY_KEY = "org.sonar.plugins.qualityprofile.progression.notification.project.key";
	public static final String NOTIFICATION_PROJECT_VIOLATIONS_KEY = "org.sonar.plugins.qualityprofile.progression.notification.project.violations";
	public static final String NOTIFICATION_PROJECT_NOTIFICATION_USERS_KEY = "org.sonar.plugins.qualityprofile.progression.notification.users";
	public static final String NOTIFICATION_ANALYSIS_VERSION_KEY = "org.sonar.plugins.qualityprofile.progression.notification.analysis.version";
	public static final String NOTIFICATION_VIOLAIONS_THRESHOLD_KEY = "org.sonar.plugins.qualityprofile.progression.notification.violations.threshold";
	public static final String NOTIFICATION_PROJECT_PROGRESSED_KEY = "org.sonar.plugins.qualityprofile.progression.notification.project.progressed";
	public static final String NOTIFICATION_MESSAGE_KEY = "org.sonar.plugins.qualityprofile.progression.notification.message";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getExtensions()
	{
		return Arrays.asList(ProfileProgressionDecorator.class, ProfileProgressedEmailTemplate.class, ProfileProgressedNotificationDispatcher.class);
	}

}
