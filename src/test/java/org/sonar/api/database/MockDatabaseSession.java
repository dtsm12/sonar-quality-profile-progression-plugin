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
package org.sonar.api.database;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.sonar.api.profiles.RulesProfile;

public class MockDatabaseSession extends DatabaseSession
{
	Map<EntityKey, Object> entities = new HashMap<EntityKey, Object>();
	Map<Class, Query> queryResults = new HashMap<Class, Query>();

	EntityManager entityManager = new MockEntityManager();

	public MockDatabaseSession()
	{
		super();
	}

	@Override
	public EntityManager getEntityManager()
	{
		return entityManager;
	}

	@Override
	public void start()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void stop()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void commit()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T save(T entity)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object saveWithoutFlush(Object entity)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(Object entity)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void save(Object... entities)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Object merge(Object entity)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(Object entity)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeWithoutFlush(Object entity)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T reattach(Class<T> entityClass, Object primaryKey)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createQuery(String hql)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createNativeQuery(String sql)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getSingleResult(Query query, T defaultValue)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getEntity(Class<T> entityClass, Object id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getSingleResult(Class<T> entityClass, Object... criterias)
	{
		return getEntityManager().find(entityClass, criterias);
	}

	@Override
	public <T> List<T> getResults(Class<T> entityClass, Object... criterias)
	{
		return getResults(entityClass);
	}

	@Override
	public <T> List<T> getResults(Class<T> entityClass)
	{
		List<T> results = Collections.EMPTY_LIST;
		Query query = queryResults.get(entityClass);
		if (query != null)
		{
			results = (List<T>) query.getResultList();
		}
		return results;
	}

	public class EntityKey
	{
		private Class clazz;
		private Object key;

		public EntityKey(Class clazz, Object key)
		{
			super();
			this.clazz = clazz;
			this.key = key;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (!(obj instanceof EntityKey))
			{
				return false;
			}
			EntityKey other = (EntityKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
			{
				return false;
			}
			if (clazz == null)
			{
				if (other.clazz != null)
				{
					return false;
				}
			}
			else if (!clazz.equals(other.clazz))
			{
				return false;
			}
			if (key == null)
			{
				if (other.key != null)
				{
					return false;
				}
			}
			else if (!key.equals(other.key))
			{
				return false;
			}
			return true;
		}

		private MockDatabaseSession getOuterType()
		{
			return MockDatabaseSession.this;
		}
	}

	private class MockEntityManager implements EntityManager
	{

		public void persist(Object entity)
		{
			// TODO Auto-generated method stub

		}

		public <T> T merge(T entity)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public void remove(Object entity)
		{
			// TODO Auto-generated method stub

		}

		public <T> T find(Class<T> entityClass, Object primaryKey)
		{
			EntityKey key = new EntityKey(entityClass, primaryKey);
			return (T) entities.get(key);
		}

		public <T> T getReference(Class<T> entityClass, Object primaryKey)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public void flush()
		{
			// TODO Auto-generated method stub

		}

		public void setFlushMode(FlushModeType flushMode)
		{
			// TODO Auto-generated method stub

		}

		public FlushModeType getFlushMode()
		{
			// TODO Auto-generated method stub
			return null;
		}

		public void lock(Object entity, LockModeType lockMode)
		{
			// TODO Auto-generated method stub

		}

		public void refresh(Object entity)
		{
			// TODO Auto-generated method stub

		}

		public void clear()
		{
			// TODO Auto-generated method stub

		}

		public boolean contains(Object entity)
		{
			// TODO Auto-generated method stub
			return false;
		}

		public Query createQuery(String ejbqlString)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Query createNamedQuery(String name)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Query createNativeQuery(String sqlString)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Query createNativeQuery(String sqlString, Class resultClass)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Query createNativeQuery(String sqlString, String resultSetMapping)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public void joinTransaction()
		{
			// TODO Auto-generated method stub

		}

		public Object getDelegate()
		{
			// TODO Auto-generated method stub
			return null;
		}

		public void close()
		{
			// TODO Auto-generated method stub

		}

		public boolean isOpen()
		{
			// TODO Auto-generated method stub
			return false;
		}

		public EntityTransaction getTransaction()
		{
			// TODO Auto-generated method stub
			return null;
		}

	}

	private class MockQuery implements Query
	{
		List results;

		public MockQuery(List results)
		{
			super();
			this.results = results;
		}

		public List getResultList()
		{
			return results;
		}

		public Object getSingleResult()
		{
			// TODO Auto-generated method stub
			return null;
		}

		public int executeUpdate()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		public Query setMaxResults(int maxResult)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Query setFirstResult(int startPosition)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Query setHint(String hintName, Object value)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Query setParameter(String name, Object value)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Query setParameter(String name, Date value, TemporalType temporalType)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Query setParameter(String name, Calendar value, TemporalType temporalType)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Query setParameter(int position, Object value)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Query setParameter(int position, Date value, TemporalType temporalType)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Query setParameter(int position, Calendar value, TemporalType temporalType)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Query setFlushMode(FlushModeType flushMode)
		{
			// TODO Auto-generated method stub
			return null;
		}

	}

	public void setEntities(Map<EntityKey, Object> entities)
	{
		this.entities = entities;
	}

	public void addMockQueryResults(Class queryClass, List results)
	{
		queryResults.put(queryClass, new MockQuery(results));
	}

}
