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
package org.sonar.api.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.sonar.api.design.Dependency;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasuresFilter;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Violation;
import org.sonar.api.violations.ViolationQuery;

public class MockDecoratorContext implements DecoratorContext
{
	private Project project;
	private Resource resource;
	private List<Violation> violations = new ArrayList<Violation>();

	public MockDecoratorContext(Project project, Resource resource, List<Violation> violations)
	{
		super();
		this.project = project;
		this.resource = resource;
		this.violations = violations;
	}

	public Project getProject()
	{
		return project;
	}

	public Resource getResource()
	{
		return resource;
	}

	public List<DecoratorContext> getChildren()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Measure getMeasure(Metric metric)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public <M> M getMeasures(MeasuresFilter<M> filter)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Measure> getChildrenMeasures(MeasuresFilter filter)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Measure> getChildrenMeasures(Metric metric)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public DecoratorContext saveMeasure(Measure measure)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public DecoratorContext saveMeasure(Metric metric, Double value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Dependency saveDependency(Dependency dependency)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Dependency> getDependencies()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Dependency> getIncomingDependencies()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Dependency> getOutgoingDependencies()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<Violation> getViolations(ViolationQuery violationQuery)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<Violation> getViolations()
	{
		return violations;
	}

	public DecoratorContext saveViolation(Violation violation, boolean force)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public DecoratorContext saveViolation(Violation violation)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<Event> getEvents()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Event createEvent(String name, String description, String category, Date date)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteEvent(Event event)
	{
		// TODO Auto-generated method stub

	}

}
