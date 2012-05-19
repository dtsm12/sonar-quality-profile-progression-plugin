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

import org.sonar.plugins.qualityprofile.progression.ProfileProgressionPlugin;
import org.sonar.plugins.qualityprofile.progression.batch.ProfileProgressionDecorator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.MockDecoratorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.database.MockDatabaseSession;
import org.sonar.api.database.MockDatabaseSession.EntityKey;
import org.sonar.api.database.model.ResourceModel;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.jpa.dao.MockProfilesDao;

//TODO add test for project specified target quality profile
public class ProfileProgressionDecoratorTest
{
  Settings settings;
  RulesProfile[] rulesProfiles;
  Project testProject;
  ResourceModel testProjectModel;
  MockDatabaseSession databaseSession;
  Map<EntityKey, Object> entities = new HashMap<EntityKey, Object>();
  DecoratorContext decoratorContext;

  @Before
  public void initialise()
  {

    // create mock db objects
    databaseSession = new MockDatabaseSession();
    databaseSession.setEntities(entities);

    // create quality profile hierarchy
    int numberOfProfiles = 4;
    int numberOfRules = 100;
    rulesProfiles = new RulesProfile[numberOfProfiles];
    for (int i = 0; i < numberOfProfiles; i++)
    {
      String profileName = "qp" + i + "0";
      rulesProfiles[i] = createRulesProfile(profileName, numberOfRules);

      if (i > 0)
      {
        rulesProfiles[i].setParentName(rulesProfiles[i - 1].getName());
      }
    }

    // create test project DTO
    Integer projectId = new Integer(123);
    testProject = new Project("test.project.key");
    testProject.setId(projectId);
    testProject.setLanguage(Java.INSTANCE);

    // create test project entity
    testProjectModel = new ResourceModel();
    testProjectModel.setId(projectId);
    testProjectModel.setRulesProfile(createRulesProfile("testProjectProfile", numberOfRules));

    // add project to db objects
    MockDatabaseSession.EntityKey projectKey = databaseSession.new EntityKey(ResourceModel.class, projectId);
    entities.put(projectKey, testProjectModel);

    // define test threshold
    int violationThreshold = 10;

    // create violations
    List<Violation> violations = new ArrayList<Violation>();
    List<ActiveRule> rules = rulesProfiles[0].getActiveRules();
    int numberOfViolationsToBUnderThreshold = ((numberOfRules / 100) * violationThreshold) - 1;
    for (int i = 0; i < numberOfViolationsToBUnderThreshold; i++)
    {
      violations.add(Violation.create(rules.get(i), testProject));
    }

    // create context
    decoratorContext = new MockDecoratorContext(testProject, testProject, violations);

    // define default settings
    settings = new Settings();
    settings.setProperty(ProfileProgressionPlugin.GLOBAL_QUALITY_PROFILE_CHANGE_ENABLED_KEY, "true");
    settings.setProperty(ProfileProgressionPlugin.PROJECT_QUALITY_PROFILE_CHANGE_ENABLED_KEY, "true");
    settings.setProperty(ProfileProgressionPlugin.GLOBAL_QUALITY_PROFILE_CHANGE_THRESHOLD_KEY, String.valueOf(violationThreshold));
    settings.setProperty(ProfileProgressionPlugin.GLOBAL_TARGET_LANGUAGE_QUALITY_PROFILE_KEY, rulesProfiles[numberOfProfiles-1].getName());
  }

  protected RulesProfile createRulesProfile(String profileName, int numberOfRules)
  {

    String languageKey = Java.KEY;
    RulesProfile rulesProfile = RulesProfile.create(profileName, languageKey);

    List<ActiveRule> activeRules = new ArrayList<ActiveRule>();
    for (int j = 0; j < numberOfRules; j++)
    {
      activeRules.add(new ActiveRule(rulesProfile, Rule.create(), RulePriority.BLOCKER));
    }
    rulesProfile.setActiveRules(activeRules);

    // add to db objects
    MockDatabaseSession.EntityKey profileKey = databaseSession.new EntityKey(RulesProfile.class, languageKey + profileName);
    entities.put(profileKey, rulesProfile);

    return rulesProfile;
  }

  protected void runAnalysis()
  {
    // create decorator
    ProfileProgressionDecorator decorator = new ProfileProgressionDecorator(databaseSession, testProjectModel.getRulesProfile(), settings);

    // provide mock profiles dao to access mock entity manager - could
    // better implement mock entity manager to avoid this
    decorator.setProfilesDao(new MockProfilesDao(databaseSession));

    if (decorator.shouldExecuteOnProject(testProject))
    {
      decorator.decorate(testProject, decoratorContext);
    }
  }

  @Test
  public void testPluginDisabledGlobally()
  {
    // disable plug-in globally
    settings.setProperty(ProfileProgressionPlugin.GLOBAL_QUALITY_PROFILE_CHANGE_ENABLED_KEY, "false");

    String profileBefore = testProjectModel.getRulesProfile().getName();
    runAnalysis();
    String profileAfter = testProjectModel.getRulesProfile().getName();

    Assert.assertEquals("Projects profile has been changed despite plug-in being disabled globally.", profileBefore, profileAfter);
  }

  @Test
  public void testPluginDisabledByProject()
  {
    // disable plug-in at project level
    settings.setProperty(ProfileProgressionPlugin.PROJECT_QUALITY_PROFILE_CHANGE_ENABLED_KEY, "false");

    String profileBefore = testProjectModel.getRulesProfile().getName();
    runAnalysis();
    String profileAfter = testProjectModel.getRulesProfile().getName();

    Assert.assertEquals("Projects profile has been changed despite plug-in being disabled by project.", profileBefore, profileAfter);
  }

  @Test
  public void testThresholdNan()
  {
    // set threshold to not a number
    settings.setProperty(ProfileProgressionPlugin.GLOBAL_QUALITY_PROFILE_CHANGE_THRESHOLD_KEY, "xx");

    // test it doesn't throw any error
    String profileBefore = testProjectModel.getRulesProfile().getName();
    runAnalysis();
    String profileAfter = testProjectModel.getRulesProfile().getName();

    Assert.assertEquals("Projects profile has been changed despite theshold not being a number.", profileBefore, profileAfter);
  }

  @Test
  public void testThresholdTooSmall()
  {
    // set threshold to negative number
    settings.setProperty(ProfileProgressionPlugin.GLOBAL_QUALITY_PROFILE_CHANGE_THRESHOLD_KEY, "-1");

    // test it doesn't throw any error
    String profileBefore = testProjectModel.getRulesProfile().getName();
    runAnalysis();
    String profileAfter = testProjectModel.getRulesProfile().getName();

    Assert.assertEquals("Projects profile has been changed despite theshold being too small.", profileBefore, profileAfter);
  }

  @Test
  public void testThresholdTooBig()
  {
    // set threshold to more than 100
    settings.setProperty(ProfileProgressionPlugin.GLOBAL_QUALITY_PROFILE_CHANGE_THRESHOLD_KEY, "101");

    // test it doesn't throw any error
    String profileBefore = testProjectModel.getRulesProfile().getName();
    runAnalysis();
    String profileAfter = testProjectModel.getRulesProfile().getName();

    Assert.assertEquals("Projects profile has been changed despite theshold being too big.", profileBefore, profileAfter);
  }

  @Test
  public void testEmptyQualityProfileList()
  {
    // set threshold to more than 100
    settings.removeProperty(ProfileProgressionPlugin.GLOBAL_TARGET_LANGUAGE_QUALITY_PROFILE_KEY);

    // test it doesn't throw any error
    String profileBefore = testProjectModel.getRulesProfile().getName();
    runAnalysis();
    String profileAfter = testProjectModel.getRulesProfile().getName();

    Assert.assertEquals("Projects profile has been changed despite the quality profile list being empty.", profileBefore, profileAfter);
  }

  @Test
  public void testProjectOverThreshold()
  {
    // add enough violations to be over threshold
    int threshold = settings.getInt(ProfileProgressionPlugin.GLOBAL_QUALITY_PROFILE_CHANGE_THRESHOLD_KEY);
    List<ActiveRule> rules = testProjectModel.getRulesProfile().getActiveRules();
    int numberOfRules = rules.size();
    List<Violation> violations = decoratorContext.getViolations();
    int initialNumberOfViolations = violations.size();

    int numberOfExtraViolations = ((threshold + 1) * (numberOfRules / 100)) - initialNumberOfViolations;

    for (int i = 0; i < numberOfExtraViolations; i++)
    {
      violations.add(Violation.create(rules.get(initialNumberOfViolations + i), testProject));

    }

    // test profile isn't progressed
    String profileBefore = testProjectModel.getRulesProfile().getName();
    runAnalysis();
    String profileAfter = testProjectModel.getRulesProfile().getName();

    Assert.assertEquals("Projects profile has been changed despite the violation threshold not met.", profileBefore, profileAfter);
  }

  @Test
  public void testProjectUnderThreshold()
  {
    // test quality profile changes
    String profileBefore = testProjectModel.getRulesProfile().getName();
    runAnalysis();
    String profileAfter = testProjectModel.getRulesProfile().getName();

    assertThat("Projects profile has not been changed despite meeting the violation threshold.", profileBefore, not(equalTo(profileAfter)));
    Assert.assertSame("Projects new profile is not the next profile in pression list.", rulesProfiles[0].getName(), profileAfter);
  }

  @Test
  public void testProjectWithManagedProfile()
  {
    // set project's profile to first managed profile
    testProjectModel.setRulesProfile(rulesProfiles[0]);

    // test quality profile changes
    String profileBefore = testProjectModel.getRulesProfile().getName();
    runAnalysis();
    String profileAfter = testProjectModel.getRulesProfile().getName();

    assertThat("Projects profile has not been changed despite meeting the violation threshold.", profileBefore, not(equalTo(profileAfter)));
    Assert.assertEquals("Projects new profile is not the next profile in pression list.", rulesProfiles[1].getName(), profileAfter);
  }

  @Test
  public void testProjectWithParentManagedProfile()
  {
    // make current test profile child of managed one
    testProjectModel.getRulesProfile().setParentName(rulesProfiles[0].getName());

    // test quality profile changes
    String parentProfileBefore = testProjectModel.getRulesProfile().getParentName();
    runAnalysis();
    String parentProfileAfter = testProjectModel.getRulesProfile().getParentName();

    assertThat("Project profile's parent has not been changed despite meeting the violation threshold.", parentProfileBefore,
        not(equalTo(parentProfileAfter)));
    Assert.assertEquals("Project profile's parent is not the next profile in pression list.", rulesProfiles[1].getName(), parentProfileAfter);
  }

  @Test
  public void testProjectWithGrandParentManagedProfile()
  {
    // create profile to be parent of project's profile
    RulesProfile testProjectParentProfile = createRulesProfile("testProjectParentProfile", 10);
    testProjectModel.getRulesProfile().setParentName(testProjectParentProfile.getName());

    // make parent profile's parent managed profile
    testProjectParentProfile.setParentName(rulesProfiles[2].getName());

    // test quality profile changes
    String grandParentProfileBefore = testProjectParentProfile.getParentName();
    runAnalysis();
    String grandParentProfileAfter = testProjectParentProfile.getParentName();

    assertThat("Project profile's grand-parent has not been changed despite meeting the violation threshold.", grandParentProfileBefore,
        not(equalTo(grandParentProfileAfter)));
    Assert.assertEquals("Project profile's grand-parent is not the next profile in pression list.", rulesProfiles[3].getName(),
        grandParentProfileAfter);
  }

  @Test
  public void testProjectWithLastManagedProfile()
  {
    // set project's profile to first managed profile
    testProjectModel.setRulesProfile(rulesProfiles[rulesProfiles.length - 1]);

    // test quality profile changes
    String profileBefore = testProjectModel.getRulesProfile().getName();
    runAnalysis();
    String profileAfter = testProjectModel.getRulesProfile().getName();

    Assert.assertEquals("Project's profile has been changed despite being last managed profile.", profileBefore, profileAfter);
  }

  @Test
  public void testProjectWithParentLastManagedProfile()
  {
    // make current test profile child of managed one
    testProjectModel.getRulesProfile().setParentName(rulesProfiles[rulesProfiles.length - 1].getName());

    // test quality profile changes
    String parentProfileBefore = testProjectModel.getRulesProfile().getParentName();
    runAnalysis();
    String parentProfileAfter = testProjectModel.getRulesProfile().getParentName();

    Assert.assertEquals("Projects profile's parent has been changed despite being last managed profile.", parentProfileBefore, parentProfileAfter);
  }

  @Test
  public void testProjectWithGrandParentLastManagedProfile()
  {
    // create profile to be parent of project's profile
    RulesProfile testProjectParentProfile = createRulesProfile("testProjectParentProfile", 10);
    testProjectModel.getRulesProfile().setParentName(testProjectParentProfile.getName());

    // make parent profile's parent managed profile
    testProjectParentProfile.setParentName(rulesProfiles[rulesProfiles.length - 1].getName());

    // test quality profile changes
    String grandParentProfileBefore = testProjectParentProfile.getParentName();
    runAnalysis();
    String grandParentProfileAfter = testProjectParentProfile.getParentName();

    Assert.assertEquals("Projects profile's grand-parent has been changed despite being last managed profile.", grandParentProfileBefore,
        grandParentProfileAfter);
  }

}
