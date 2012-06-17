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

import java.util.Iterator;

import org.slf4j.Logger;
import org.sonar.api.utils.ValidationMessages;

public class ValidationUtil
{
	public static void log(Logger logger, ValidationMessages messages, String messageTemplate, Object... logValues)
	{
		for (Iterator<String> iterator = messages.getErrors().iterator(); iterator.hasNext();)
		{
			logger.error(messageTemplate, logValues, iterator.next());
		}
		for (Iterator<String> iterator = messages.getWarnings().iterator(); iterator.hasNext();)
		{
			logger.warn(messageTemplate, logValues, iterator.next());
		}
		for (Iterator<String> iterator = messages.getInfos().iterator(); iterator.hasNext();)
		{
			logger.info(messageTemplate, logValues, iterator.next());
		}
	}
}
