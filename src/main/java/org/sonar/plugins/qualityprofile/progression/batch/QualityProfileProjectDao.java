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
package org.sonar.plugins.qualityprofile.progression.batch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sonar.api.database.DatabaseSession;
import org.sonar.api.database.model.ResourceModel;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.jpa.dao.BaseDao;

public class QualityProfileProjectDao extends BaseDao
{

	public QualityProfileProjectDao(DatabaseSession session)
	{
		super(session);
	}

	public Set<ResourceModel> getAllProjectsUsingQualityProfile(RulesProfile rulesProfile)
	{
		// initialise project set
		Set<ResourceModel> projects = new HashSet<ResourceModel>();

		// recurse down quality profiles getting projects using them
		addProjectsUsingQualityProfile(rulesProfile, projects);

		return projects;
	}

	protected void addProjectsUsingQualityProfile(RulesProfile rulesProfile, Set<ResourceModel> projects)
	{
		// add all projects using that profile
		projects.addAll(getSession().getResults(ResourceModel.class, "rulesProfile", rulesProfile, "scope", ResourceModel.SCOPE_PROJECT));

		// get children of profile
		List<RulesProfile> children = getSession().getResults(RulesProfile.class, "parentName", rulesProfile.getName());
		for (Iterator<RulesProfile> iterator = children.iterator(); iterator.hasNext();)
		{
			RulesProfile childRulesProfile = iterator.next();
			addProjectsUsingQualityProfile(childRulesProfile, projects);
		}
	}
}
