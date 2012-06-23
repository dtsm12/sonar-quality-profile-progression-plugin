sonar-quality-profile-progression-plugin
========================================

Sonar plugin to progress a project's quality profile based on violations threshold.
I have added the mvn-repo directory so this Git repo can act as as a mvn repo too.

Additional Quality Profile Hierarchies
-------------------------------------
To aid the creation plug-ins for other profile hierarchy the following steps can be take:

1) Create class which represents the profile hierarchy
	It should implement org.sonar.plugins.qualityprofileprogression.api.QualityProfileProgressionHierarchy
	e.g. org.sonar.plugins.qualityprofileprogression.profile.SonarWayWithFindbugsProfileProgressionHierarchy

2) Create a class per profile in the hierarchy
	It should extend org.sonar.plugins.qualityprofileprogression.api.HierarchicalProfileDefinition
	It must call the HierarchicalProfileDefinition constructor
	Typically you would create a sub-class of HierarchicalProfileDefinition for each rules engine type
	And then a sub-class of that class per set of rules
	e.g. org.sonar.plugins.qualityprofileprogression.profile.SonarWayWithFindbugsSubSetProfile
	e.g. org.sonar.plugins.qualityprofileprogression.profile.checkstyle.SonarWayWithFindbugsSubSetCheckStyleProfile
	e.g. org.sonar.plugins.qualityprofileprogression.profile.checkstyle.SonarWayWithFindbugsSubSetCheckStyleSet1Profile
	e.g. org.sonar.plugins.qualityprofileprogression.profile.checkstyle.SonarWayWithFindbugsSubSetCheckStyleSet2Profile

3) Create a class to fix the profile inheritance on start up
	It should implement org.sonar.api.platform.ServerStartHandler
	It should instantiate org.sonar.plugins.qualityprofileprogression.api.ProfileRulesInheritanceUpdater in its constructor
	It should invoke ProfileRulesInheritanceUpdater.updateRulesInheritance in its onServerStart method
	e.g. org.sonar.plugins.qualityprofileprogression.profile.ServerStartProfileInheritanceUpdater

4) Return the following classes from plug-in class's getExtensions method:
	* the profile importer(s)
	* the specific set profile classes
	* the server start updater class
	e.g. org.sonar.plugins.qualityprofileprogression.ProfileProgressionPlugin