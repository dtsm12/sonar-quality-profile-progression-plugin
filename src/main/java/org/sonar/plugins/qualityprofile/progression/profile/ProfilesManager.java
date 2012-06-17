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
package org.sonar.plugins.qualityprofile.progression.profile;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.plexus.util.StringUtils;
import org.sonar.api.database.DatabaseSession;
import org.sonar.api.database.model.ResourceModel;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.*;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.jpa.dao.BaseDao;
import org.sonar.jpa.dao.RulesDao;

import java.util.List;

public class ProfilesManager extends BaseDao
{

	private RulesDao rulesDao;

	public ProfilesManager(DatabaseSession session, RulesDao rulesDao)
	{
		super(session);
		this.rulesDao = rulesDao;
	}

	public void renameProfile(int profileId, String newProfileName)
	{
		RulesProfile profile = getSession().getSingleResult(RulesProfile.class, "id", profileId);
		if (profile != null && !profile.getProvided())
		{
			String hql = "UPDATE " + RulesProfile.class.getSimpleName() + " o SET o.parentName=:newName  WHERE o.parentName=:oldName";
			getSession().getEntityManager().createQuery(hql).setParameter("oldName", profile.getName()).setParameter("newName", newProfileName)
					.executeUpdate();
			profile.setName(newProfileName);
			getSession().save(profile);
			getSession().commit();
		}
	}

	public void copyProfile(int profileId, String newProfileName)
	{
		RulesProfile profile = getSession().getSingleResult(RulesProfile.class, "id", profileId);
		RulesProfile toImport = (RulesProfile) profile.clone();
		toImport.setName(newProfileName);
		toImport.setDefaultProfile(false);
		toImport.setProvided(false);
		ProfilesBackup pb = new ProfilesBackup(getSession());
		pb.importProfile(rulesDao, toImport);
		getSession().commit();
	}

	public void deleteProfile(int profileId)
	{
		RulesProfile profile = getSession().getEntity(RulesProfile.class, profileId);
		if (profile != null && !profile.getProvided() && getChildren(profile).isEmpty())
		{
			// Remove history of rule changes
			String hqlDeleteRc = "DELETE " + ActiveRuleChange.class.getSimpleName() + " rc WHERE rc.rulesProfile=:rulesProfile";
			getSession().createQuery(hqlDeleteRc).setParameter("rulesProfile", profile).executeUpdate();

			String hql = "UPDATE " + ResourceModel.class.getSimpleName() + " o SET o.rulesProfile=null WHERE o.rulesProfile=:rulesProfile";
			getSession().createQuery(hql).setParameter("rulesProfile", profile).executeUpdate();
			getSession().remove(profile);
			getSession().commit();
		}
	}

	public void deleteAllProfiles()
	{
		// Remove history of rule changes
		String hqlDeleteRc = "DELETE " + ActiveRuleChange.class.getSimpleName() + " rc";
		getSession().createQuery(hqlDeleteRc).executeUpdate();

		String hql = "UPDATE " + ResourceModel.class.getSimpleName() + " o SET o.rulesProfile = null WHERE o.rulesProfile IS NOT NULL";
		getSession().createQuery(hql).executeUpdate();
		List profiles = getSession().createQuery("FROM " + RulesProfile.class.getSimpleName()).getResultList();
		for (Object profile : profiles)
		{
			getSession().removeWithoutFlush(profile);
		}
		getSession().commit();
	}

	// Managing inheritance of profiles

	public ValidationMessages changeParentProfile(Integer profileId, String parentName, String userName)
	{
		ValidationMessages messages = ValidationMessages.create();
		RulesProfile profile = getSession().getEntity(RulesProfile.class, profileId);
		if (profile != null)
		{
			RulesProfile oldParent = getParentProfile(profile);
			RulesProfile newParent = getProfile(profile.getLanguage(), parentName);
			if (isCycle(profile, newParent))
			{
				messages.addWarningText("Please do not select a child profile as parent.");
				return messages;
			}
			// Deactivate all inherited rules
			if (oldParent != null)
			{
				for (ActiveRule activeRule : oldParent.getActiveRules())
				{
					deactivate(profile, activeRule.getRule(), userName);
				}
			}
			// Activate all inherited rules
			if (newParent != null)
			{
				for (ActiveRule activeRule : newParent.getActiveRules())
				{
					activateOrChange(profile, activeRule, userName);
				}
			}
			profile.setParentName(newParent == null ? null : newParent.getName());
			getSession().saveWithoutFlush(profile);
			getSession().commit();
		}
		return messages;
	}

	/**
	 * Rule was activated
	 */
	public void activated(int profileId, int activeRuleId, String userName)
	{
		ActiveRule activeRule = getSession().getEntity(ActiveRule.class, activeRuleId);
		RulesProfile profile = getSession().getEntity(RulesProfile.class, profileId);
		ruleEnabled(profile, activeRule, userName);
		// Notify child profiles
		activatedOrChanged(profileId, activeRuleId, userName);
	}

	/**
	 * Rule param was changed
	 */
	public void ruleParamChanged(int profileId, int activeRuleId, String paramKey, String oldValue, String newValue, String userName)
	{
		ActiveRule activeRule = getSession().getEntity(ActiveRule.class, activeRuleId);
		RulesProfile profile = getSession().getEntity(RulesProfile.class, profileId);

		ruleParamChanged(profile, activeRule.getRule(), paramKey, oldValue, newValue, userName);

		// Notify child profiles
		activatedOrChanged(profileId, activeRuleId, userName);
	}

	/**
	 * Rule severity was changed
	 */
	public void ruleSeverityChanged(int profileId, int activeRuleId, RulePriority oldSeverity, RulePriority newSeverity, String userName)
	{
		ActiveRule activeRule = getSession().getEntity(ActiveRule.class, activeRuleId);
		RulesProfile profile = getSession().getEntity(RulesProfile.class, profileId);

		ruleSeverityChanged(profile, activeRule.getRule(), oldSeverity, newSeverity, userName);

		// Notify child profiles
		activatedOrChanged(profileId, activeRuleId, userName);
	}

	/**
	 * Rule was activated/changed in parent profile.
	 */
	private void activatedOrChanged(int parentProfileId, int activeRuleId, String userName)
	{
		ActiveRule parentActiveRule = getSession().getEntity(ActiveRule.class, activeRuleId);
		if (parentActiveRule.isInherited())
		{
			parentActiveRule.setInheritance(ActiveRule.OVERRIDES);
			getSession().saveWithoutFlush(parentActiveRule);
		}
		for (RulesProfile child : getChildren(parentProfileId))
		{
			activateOrChange(child, parentActiveRule, userName);
		}
		getSession().commit();
	}

	/**
	 * Rule was deactivated in parent profile.
	 */
	public void deactivated(int parentProfileId, int deactivatedRuleId, String userName)
	{
		ActiveRule parentActiveRule = getSession().getEntity(ActiveRule.class, deactivatedRuleId);
		RulesProfile profile = getSession().getEntity(RulesProfile.class, parentProfileId);
		ruleDisabled(profile, parentActiveRule, userName);
		for (RulesProfile child : getChildren(parentProfileId))
		{
			deactivate(child, parentActiveRule.getRule(), userName);
		}
		getSession().commit();
	}

	/**
	 * @return true, if setting <code>childProfile</code> as a child of
	 *         <code>parentProfile</code> adds cycle
	 */
	boolean isCycle(RulesProfile childProfile, RulesProfile parentProfile)
	{
		while (parentProfile != null)
		{
			if (childProfile.equals(parentProfile))
			{
				return true;
			}
			parentProfile = getParentProfile(parentProfile);
		}
		return false;
	}

	public void revert(int profileId, int activeRuleId, String userName)
	{
		RulesProfile profile = getSession().getEntity(RulesProfile.class, profileId);
		ActiveRule oldActiveRule = getSession().getEntity(ActiveRule.class, activeRuleId);
		if (oldActiveRule != null && oldActiveRule.doesOverride())
		{
			ActiveRule parentActiveRule = getParentProfile(profile).getActiveRule(oldActiveRule.getRule());
			removeActiveRule(profile, oldActiveRule);
			ActiveRule newActiveRule = (ActiveRule) parentActiveRule.clone();
			newActiveRule.setRulesProfile(profile);
			newActiveRule.setInheritance(ActiveRule.INHERITED);
			profile.addActiveRule(newActiveRule);
			getSession().saveWithoutFlush(newActiveRule);

			// Compute change
			ruleChanged(profile, oldActiveRule, newActiveRule, userName);

			for (RulesProfile child : getChildren(profile))
			{
				activateOrChange(child, newActiveRule, userName);
			}

			getSession().commit();
		}
	}

	private synchronized void incrementProfileVersionIfNeeded(RulesProfile profile)
	{
		if (profile.getUsed())
		{
			profile.setVersion(profile.getVersion() + 1);
			profile.setUsed(false);
			getSession().saveWithoutFlush(profile);
		}
	}

	/**
	 * Deal with creation of ActiveRuleChange item when a rule param is changed
	 * on a profile
	 */
	private void ruleParamChanged(RulesProfile profile, Rule rule, String paramKey, String oldValue, String newValue, String userName)
	{
		incrementProfileVersionIfNeeded(profile);
		ActiveRuleChange rc = new ActiveRuleChange(userName, profile, rule);
		if (!StringUtils.equals(oldValue, newValue))
		{
			rc.setParameterChange(paramKey, oldValue, newValue);
			getSession().saveWithoutFlush(rc);
		}
	}

	/**
	 * Deal with creation of ActiveRuleChange item when a rule severity is
	 * changed on a profile
	 */
	private void ruleSeverityChanged(RulesProfile profile, Rule rule, RulePriority oldSeverity, RulePriority newSeverity, String userName)
	{
		incrementProfileVersionIfNeeded(profile);
		ActiveRuleChange rc = new ActiveRuleChange(userName, profile, rule);
		if (!ObjectUtils.equals(oldSeverity, newSeverity))
		{
			rc.setOldSeverity(oldSeverity);
			rc.setNewSeverity(newSeverity);
			getSession().saveWithoutFlush(rc);
		}
	}

	/**
	 * Deal with creation of ActiveRuleChange item when a rule is changed
	 * (severity and/or param(s)) on a profile
	 */
	private void ruleChanged(RulesProfile profile, ActiveRule oldActiveRule, ActiveRule newActiveRule, String userName)
	{
		incrementProfileVersionIfNeeded(profile);
		ActiveRuleChange rc = new ActiveRuleChange(userName, profile, newActiveRule.getRule());

		if (oldActiveRule.getSeverity() != newActiveRule.getSeverity())
		{
			rc.setOldSeverity(oldActiveRule.getSeverity());
			rc.setNewSeverity(newActiveRule.getSeverity());
		}
		if (oldActiveRule.getRule().getParams() != null)
		{
			for (RuleParam p : oldActiveRule.getRule().getParams())
			{
				String oldParam = oldActiveRule.getParameter(p.getKey());
				String newParam = newActiveRule.getParameter(p.getKey());
				if (!StringUtils.equals(oldParam, newParam))
				{
					rc.setParameterChange(p.getKey(), oldParam, newParam);
				}
			}
		}

		getSession().saveWithoutFlush(rc);
	}

	/**
	 * Deal with creation of ActiveRuleChange item when a rule is enabled on a
	 * profile
	 */
	private void ruleEnabled(RulesProfile profile, ActiveRule newActiveRule, String userName)
	{
		incrementProfileVersionIfNeeded(profile);
		ActiveRuleChange rc = new ActiveRuleChange(userName, profile, newActiveRule.getRule());
		rc.setEnabled(true);
		rc.setNewSeverity(newActiveRule.getSeverity());
		if (newActiveRule.getRule().getParams() != null)
		{
			for (RuleParam p : newActiveRule.getRule().getParams())
			{
				String newParam = newActiveRule.getParameter(p.getKey());
				if (newParam != null)
				{
					rc.setParameterChange(p.getKey(), null, newParam);
				}
			}
		}
		getSession().saveWithoutFlush(rc);
	}

	/**
	 * Deal with creation of ActiveRuleChange item when a rule is disabled on a
	 * profile
	 */
	private void ruleDisabled(RulesProfile profile, ActiveRule disabledRule, String userName)
	{
		incrementProfileVersionIfNeeded(profile);
		ActiveRuleChange rc = new ActiveRuleChange(userName, profile, disabledRule.getRule());
		rc.setEnabled(false);
		rc.setOldSeverity(disabledRule.getSeverity());
		if (disabledRule.getRule().getParams() != null)
		{
			for (RuleParam p : disabledRule.getRule().getParams())
			{
				String oldParam = disabledRule.getParameter(p.getKey());
				if (oldParam != null)
				{
					rc.setParameterChange(p.getKey(), oldParam, null);
				}
			}
		}
		getSession().saveWithoutFlush(rc);
	}

	private void activateOrChange(RulesProfile profile, ActiveRule parentActiveRule, String userName)
	{
		ActiveRule oldActiveRule = profile.getActiveRule(parentActiveRule.getRule());
		if (oldActiveRule != null)
		{
			if (oldActiveRule.isInherited())
			{
				removeActiveRule(profile, oldActiveRule);
			}
			else
			{
				oldActiveRule.setInheritance(ActiveRule.OVERRIDES);
				getSession().saveWithoutFlush(oldActiveRule);
				return; // no need to change in children
			}
		}
		ActiveRule newActiveRule = (ActiveRule) parentActiveRule.clone();
		newActiveRule.setRulesProfile(profile);
		newActiveRule.setInheritance(ActiveRule.INHERITED);
		profile.addActiveRule(newActiveRule);
		getSession().saveWithoutFlush(newActiveRule);

		if (oldActiveRule != null)
		{
			ruleChanged(profile, oldActiveRule, newActiveRule, userName);
		}
		else
		{
			ruleEnabled(profile, newActiveRule, userName);
		}

		for (RulesProfile child : getChildren(profile))
		{
			activateOrChange(child, newActiveRule, userName);
		}
	}

	private void deactivate(RulesProfile profile, Rule rule, String userName)
	{
		ActiveRule activeRule = profile.getActiveRule(rule);
		if (activeRule != null)
		{
			if (activeRule.isInherited())
			{
				ruleDisabled(profile, activeRule, userName);
				removeActiveRule(profile, activeRule);
			}
			else
			{
				activeRule.setInheritance(null);
				getSession().saveWithoutFlush(activeRule);
				return; // no need to change in children
			}

			for (RulesProfile child : getChildren(profile))
			{
				deactivate(child, rule, userName);
			}
		}
	}

	private List<RulesProfile> getChildren(int parentId)
	{
		RulesProfile parent = getSession().getEntity(RulesProfile.class, parentId);
		return getChildren(parent);
	}

	private List<RulesProfile> getChildren(RulesProfile parent)
	{
		return getSession().getResults(RulesProfile.class, "language", parent.getLanguage(), "parentName", parent.getName(), "provided", false,
				"enabled", true);
	}

	private void removeActiveRule(RulesProfile profile, ActiveRule activeRule)
	{
		profile.removeActiveRule(activeRule);
		getSession().removeWithoutFlush(activeRule);
	}

	RulesProfile getProfile(String language, String name)
	{
		return getSession().getSingleResult(RulesProfile.class, "language", language, "name", name, "enabled", true);
	}

	RulesProfile getParentProfile(RulesProfile profile)
	{
		if (profile.getParentName() == null)
		{
			return null;
		}
		return getProfile(profile.getLanguage(), profile.getParentName());
	}

}
