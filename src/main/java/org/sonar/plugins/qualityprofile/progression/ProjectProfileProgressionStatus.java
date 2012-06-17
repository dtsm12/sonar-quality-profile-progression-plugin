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

import org.sonar.api.database.model.ResourceModel;
import org.sonar.api.profiles.RulesProfile;

public class ProjectProfileProgressionStatus
{
	private boolean shouldBeProgressed = true;
	private ResourceModel project;
	private String notificationMessage;
	private int projectViolationPercentage;
	private RulesProfile profileToUpdate;
	private String nextProfileName;
	private String analysisVersion;

	public boolean profileShouldBeProgressed()
	{
		return shouldBeProgressed;
	}
	public void setProfileShouldBeProgressed(boolean shouldBeProgressed)
	{
		this.shouldBeProgressed = shouldBeProgressed;
	}
	public ResourceModel getProject()
	{
		return project;
	}
	public void setProject(ResourceModel project)
	{
		this.project = project;
	}
	public String getNotificationMessage()
	{
		return notificationMessage;
	}
	public void setNotificationMessage(String notificationMessage)
	{
		this.notificationMessage = notificationMessage;
	}
	public int getProjectViolationPercentage()
	{
		return projectViolationPercentage;
	}
	public void setProjectViolationPercentage(int projectViolationPercentage)
	{
		this.projectViolationPercentage = projectViolationPercentage;
	}
	public RulesProfile getProfileToUpdate()
	{
		return profileToUpdate;
	}
	public void setProfileToUpdate(RulesProfile profileToUpdate)
	{
		this.profileToUpdate = profileToUpdate;
	}
	public String getNextProfileName()
	{
		return nextProfileName;
	}
	public void setNextProfileName(String nextProfileName)
	{
		this.nextProfileName = nextProfileName;
	}
	public String getAnalysisVersion()
	{
		return analysisVersion;
	}
	public void setAnalysisVersion(String analysisVersion)
	{
		this.analysisVersion = analysisVersion;
	}
}
