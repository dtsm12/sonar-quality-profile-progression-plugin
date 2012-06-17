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
/*     */package org.sonar.plugins.qualityprofile.progression.profile.findbugs.xml;

/*     */
/*     */import com.thoughtworks.xstream.annotations.XStreamAlias;
/*     */
import com.thoughtworks.xstream.annotations.XStreamImplicit;
/*     */
import java.util.ArrayList;
/*     */
import java.util.List;

/*     */
/*     */@XStreamAlias("Or")
/*     */public class OrFilter
/*     */
{
	/*     */
	/*     */@XStreamImplicit(itemFieldName = "Bug")
	/*     */private List<Bug> bugs;
	/*     */
	/*     */@XStreamImplicit(itemFieldName = "Package")
	/*     */private List<PackageFilter> packages;
	/*     */
	/*     */@XStreamImplicit(itemFieldName = "Class")
	/*     */private List<ClassFilter> classes;
	/*     */
	/*     */@XStreamImplicit(itemFieldName = "Method")
	/*     */private List<MethodFilter> methods;
	/*     */
	/*     */@XStreamImplicit(itemFieldName = "Field")
	/*     */private List<FieldFilter> fields;
	/*     */
	/*     */@XStreamImplicit(itemFieldName = "Local")
	/*     */private List<LocalFilter> locals;

	/*     */
	/*     */public OrFilter()
	/*     */
	{
		/* 52 */this.bugs = new ArrayList<Bug>();
		/* 53 */this.packages = new ArrayList<PackageFilter>();
		/* 54 */this.classes = new ArrayList<ClassFilter>();
		/* 55 */this.methods = new ArrayList<MethodFilter>();
		/* 56 */this.fields = new ArrayList<FieldFilter>();
		/* 57 */this.locals = new ArrayList<LocalFilter>();
		/*     */}

	/*     */
	/*     */public List<Bug> getBugs()
	{
		/* 61 */return this.bugs;
		/*     */}

	/*     */
	/*     */public void setBugs(List<Bug> bugs)
	{
		/* 65 */this.bugs = bugs;
		/*     */}

	/*     */
	/*     */public List<PackageFilter> getPackages()
	{
		/* 69 */return this.packages;
		/*     */}

	/*     */
	/*     */public void setPackages(List<PackageFilter> packages)
	{
		/* 73 */this.packages = packages;
		/*     */}

	/*     */
	/*     */public List<ClassFilter> getClasses()
	{
		/* 77 */return this.classes;
		/*     */}

	/*     */
	/*     */public void setClasses(List<ClassFilter> classes)
	{
		/* 81 */this.classes = classes;
		/*     */}

	/*     */
	/*     */public List<MethodFilter> getMethods()
	{
		/* 85 */return this.methods;
		/*     */}

	/*     */
	/*     */public void setMethods(List<MethodFilter> methods)
	{
		/* 89 */this.methods = methods;
		/*     */}

	/*     */
	/*     */public List<FieldFilter> getFields()
	{
		/* 93 */return this.fields;
		/*     */}

	/*     */
	/*     */public void setFields(List<FieldFilter> fields)
	{
		/* 97 */this.fields = fields;
		/*     */}

	/*     */
	/*     */public List<LocalFilter> getLocals()
	{
		/* 101 */return this.locals;
		/*     */}

	/*     */
	/*     */public void setLocals(List<LocalFilter> locals)
	{
		/* 105 */this.locals = locals;
		/*     */}
	/*     */
}

/*
 * Location: C:\Users\David\AppData\Local\Temp\Rar$DIa0.527\ Qualified Name:
 * org.sonar.plugins.findbugs.xml.OrFilter JD-Core Version: 0.6.0
 */