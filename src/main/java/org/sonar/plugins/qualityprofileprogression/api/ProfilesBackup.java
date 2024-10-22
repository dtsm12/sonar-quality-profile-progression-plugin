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
package org.sonar.plugins.qualityprofileprogression.api;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.LoggerFactory;
import org.sonar.api.database.DatabaseSession;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.Alert;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.*;
import org.sonar.jpa.dao.RulesDao;

import java.util.*;

public class ProfilesBackup implements Backupable
{

	private Collection<RulesProfile> profiles;
	private DatabaseSession session;

	public ProfilesBackup(DatabaseSession session)
	{
		this.session = session;
	}

	/**
	 * for unit tests
	 */
	ProfilesBackup(Collection<RulesProfile> profiles)
	{
		this.profiles = profiles;
	}

	public void configure(XStream xStream)
	{
		xStream.alias("profile", RulesProfile.class);
		xStream.alias("alert", Alert.class);
		xStream.alias("active-rule", ActiveRule.class);
		xStream.aliasField("active-rules", RulesProfile.class, "activeRules");
		xStream.aliasField("default-profile", RulesProfile.class, "defaultProfile");
		xStream.omitField(RulesProfile.class, "id");
		xStream.omitField(RulesProfile.class, "projects");
		xStream.registerConverter(getActiveRuleConverter());
		xStream.registerConverter(getAlertsConverter());
	}

	public void exportXml(SonarConfig sonarConfig)
	{
		this.profiles = (this.profiles == null ? session.getResults(RulesProfile.class) : this.profiles);
		// the profiles objects must be cloned to avoid issues CGLib
		List<RulesProfile> cloned = new ArrayList<RulesProfile>();
		for (RulesProfile profile : this.profiles)
		{
			cloned.add((RulesProfile) profile.clone());
		}

		sonarConfig.setProfiles(cloned);
	}

	public void importXml(SonarConfig sonarConfig)
	{
		if (CollectionUtils.isNotEmpty(sonarConfig.getProfiles()))
		{
			LoggerFactory.getLogger(getClass()).info("Delete profiles");
			ProfilesManager profilesManager = new ProfilesManager(session, null);
			profilesManager.deleteAllProfiles();

			RulesDao rulesDao = new RulesDao(session);
			for (RulesProfile profile : sonarConfig.getProfiles())
			{
				LoggerFactory.getLogger(getClass()).info("Restore profile " + profile.getName());
				importProfile(rulesDao, profile);
			}
		}
	}

	public void importProfile(RulesDao rulesDao, RulesProfile toImport)
	{
		if (toImport.getEnabled() == null)
		{
			// backward-compatibility with versions < 2.6. The field "enabled"
			// did not exist. Default value is true.
			toImport.setEnabled(true);
		}
		if (toImport.getVersion() == 0)
		{
			// backward-compatibility with versions < 2.9. The field "version"
			// did not exist. Default value is 1.
			toImport.setVersion(1);
		}
		if (toImport.getUsed() == null)
		{
			// backward-compatibility with versions < 2.9. The field
			// "used_profile" did not exist. Default value is false.
			toImport.setUsed(false);
		}
		importActiveRules(rulesDao, toImport);
		importAlerts(toImport);
		session.save(toImport);
	}

	private void importAlerts(RulesProfile profile)
	{
		if (profile.getAlerts() != null)
		{
			for (Iterator<Alert> ia = profile.getAlerts().iterator(); ia.hasNext();)
			{
				Alert alert = ia.next();
				Metric unMarshalledMetric = alert.getMetric();
				String validKey = unMarshalledMetric.getKey();
				Metric matchingMetricInDb = session.getSingleResult(Metric.class, "key", validKey);
				if (matchingMetricInDb == null)
				{
					LoggerFactory.getLogger(getClass()).error("Unable to find metric " + validKey);
					ia.remove();
					continue;
				}
				alert.setMetric(matchingMetricInDb);
				alert.setRulesProfile(profile);
			}
		}
	}

	private void importActiveRules(RulesDao rulesDao, RulesProfile profile)
	{
		for (Iterator<ActiveRule> iar = profile.getActiveRules(true).iterator(); iar.hasNext();)
		{
			ActiveRule activeRule = iar.next();
			Rule unMarshalledRule = activeRule.getRule();
			Rule matchingRuleInDb = rulesDao.getRuleByKey(unMarshalledRule.getRepositoryKey(), unMarshalledRule.getKey());
			if (matchingRuleInDb == null)
			{
				LoggerFactory.getLogger(getClass()).error(
						"Unable to find active rule " + unMarshalledRule.getRepositoryKey() + ":" + unMarshalledRule.getKey());
				iar.remove();
				continue;
			}
			activeRule.setRule(matchingRuleInDb);
			activeRule.setRulesProfile(profile);
			activeRule.getActiveRuleParams();
			for (Iterator<ActiveRuleParam> irp = activeRule.getActiveRuleParams().iterator(); irp.hasNext();)
			{
				ActiveRuleParam activeRuleParam = irp.next();
				RuleParam unMarshalledRP = activeRuleParam.getRuleParam();
				RuleParam matchingRPInDb = rulesDao.getRuleParam(matchingRuleInDb, unMarshalledRP.getKey());
				if (matchingRPInDb == null)
				{
					LoggerFactory.getLogger(getClass()).error("Unable to find active rule parameter " + unMarshalledRP.getKey());
					irp.remove();
					continue;
				}
				activeRuleParam.setActiveRule(activeRule);
				activeRuleParam.setRuleParam(matchingRPInDb);
			}
		}
	}

	private Converter getAlertsConverter()
	{
		return new Converter()
		{

			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context)
			{
				Alert alert = (Alert) source;
				writeNode(writer, "operator", alert.getOperator());
				writeNode(writer, "value-error", alert.getValueError());
				writeNode(writer, "value-warning", alert.getValueWarning());
				writeNode(writer, "metric-key", alert.getMetric().getKey());
			}

			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
			{
				Map<String, String> values = readNode(reader);
				return new Alert(null, new Metric(values.get("metric-key")), values.get("operator"), values.get("value-error"),
						values.get("value-warning"));
			}

			public boolean canConvert(Class type)
			{
				return type.equals(Alert.class);
			}
		};
	}

	private Converter getActiveRuleConverter()
	{
		return new Converter()
		{

			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context)
			{
				ActiveRule rule = (ActiveRule) source;
				writeNode(writer, "key", rule.getRule().getKey());
				writeNode(writer, "plugin", rule.getRule().getRepositoryKey());
				writeNode(writer, "level", rule.getSeverity().name());
				writeNode(writer, "inheritance", rule.getInheritance());

				if (!rule.getActiveRuleParams().isEmpty())
				{
					writer.startNode("params");
					for (ActiveRuleParam activeRuleParam : rule.getActiveRuleParams())
					{
						writer.startNode("param");
						writeNode(writer, "key", activeRuleParam.getRuleParam().getKey());
						writeNode(writer, "value", activeRuleParam.getValue());
						writer.endNode();
					}
					writer.endNode();
				}
			}

			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
			{
				Map<String, String> valuesRule = new HashMap<String, String>();
				List<ActiveRuleParam> params = new ArrayList<ActiveRuleParam>();
				while (reader.hasMoreChildren())
				{
					reader.moveDown();
					valuesRule.put(reader.getNodeName(), reader.getValue());
					if ("params".equals(reader.getNodeName()))
					{
						while (reader.hasMoreChildren())
						{
							reader.moveDown();
							Map<String, String> valuesParam = readNode(reader);
							ActiveRuleParam activeRuleParam = new ActiveRuleParam(null, new RuleParam(null, valuesParam.get("key"), null, null),
									valuesParam.get("value"));
							params.add(activeRuleParam);
							reader.moveUp();
						}
					}
					reader.moveUp();
				}

				ActiveRule activeRule = new ActiveRule(null, new Rule(valuesRule.get("plugin"), valuesRule.get("key")),
						RulePriority.valueOf(valuesRule.get("level")));
				activeRule.setActiveRuleParams(params);
				if (valuesRule.containsKey("inheritance"))
				{
					activeRule.setInheritance(valuesRule.get("inheritance"));
				}
				return activeRule;
			}

			public boolean canConvert(Class type)
			{
				return type.equals(ActiveRule.class);
			}
		};
	}

	private void writeNode(HierarchicalStreamWriter writer, String name, String value)
	{
		if (value != null)
		{
			writer.startNode(name);
			writer.setValue(value);
			writer.endNode();
		}
	}

	private Map<String, String> readNode(HierarchicalStreamReader reader)
	{
		Map<String, String> values = new HashMap<String, String>();
		while (reader.hasMoreChildren())
		{
			reader.moveDown();
			values.put(reader.getNodeName(), reader.getValue());
			reader.moveUp();
		}
		return values;
	}

}
