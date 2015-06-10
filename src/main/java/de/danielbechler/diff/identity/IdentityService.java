/*
 * Copyright 2014 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff.identity;

import java.util.HashMap;
import java.util.Map;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.comparison.ComparableComparisonStrategy;
import de.danielbechler.diff.comparison.ComparisonConfigurer;
import de.danielbechler.diff.comparison.ComparisonStrategy;
import de.danielbechler.diff.comparison.ComparisonStrategyResolver;
import de.danielbechler.diff.comparison.EqualsOnlyComparisonStrategy;
import de.danielbechler.diff.comparison.ObjectDiffPropertyComparisonStrategyResolver;
import de.danielbechler.diff.comparison.PrimitiveDefaultValueMode;
import de.danielbechler.diff.comparison.PrimitiveDefaultValueModeResolver;
import de.danielbechler.diff.introspection.ObjectDiffEqualsOnlyType;
import de.danielbechler.diff.introspection.ObjectDiffProperty;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.path.NodePath;
import de.danielbechler.diff.path.NodePathValueHolder;
import de.danielbechler.util.Classes;

/**
 * Resolves identity strategies, if none specified use EqualsIdentityStrategy().
 */
public class IdentityService implements IdentityConfigurer, IdentityStrategyResolver
{
	public static final IdentityStrategy EQUALS_IDENTITY_STRATEGY = new EqualsIdentityStrategy();

	private final NodePathValueHolder<IdentityStrategy> nodePathStrategies = NodePathValueHolder.of(IdentityStrategy.class);
	private final Map<Class<?>, IdentityStrategy> typeStrategyMap = new HashMap<Class<?>, IdentityStrategy>();
	private final ObjectDifferBuilder objectDifferBuilder;

	public IdentityService(final ObjectDifferBuilder objectDifferBuilder)
	{
		this.objectDifferBuilder = objectDifferBuilder;
	}

	public IdentityStrategy resolveIdentityStrategy(final DiffNode node)
	{
		final IdentityStrategy identityStrategy = nodePathStrategies.valueForNodePath(node.getPath());
		if (identityStrategy != null)
		{
			return identityStrategy;
		}

		final Class<?> valueType = node.getValueType();
		if (typeStrategyMap.containsKey(valueType))
		{
			return typeStrategyMap.get(valueType);
		}

		return EQUALS_IDENTITY_STRATEGY;
	}

	public Of ofNode(final NodePath nodePath)
	{
		return new OfNodePath(nodePath);
	}

	public Of ofType(final Class<?> type)
	{
		return new OfType(type);
	}

	public ObjectDifferBuilder and()
	{
		return objectDifferBuilder;
	}

	private abstract static class AbstractOf implements Of
	{
	}

	private class OfType extends AbstractOf
	{
		private final Class<?> type;

		public OfType(final Class<?> type)
		{
			this.type = type;
		}

		public IdentityConfigurer toUse(final IdentityStrategy identityStrategy)
		{
			typeStrategyMap.put(type, identityStrategy);
			return IdentityService.this;
		}
	}

	private class OfNodePath extends AbstractOf
	{
		private final NodePath nodePath;

		public OfNodePath(final NodePath nodePath)
		{
			this.nodePath = nodePath;
		}

		public IdentityConfigurer toUse(final IdentityStrategy identityStrategy)
		{
			nodePathStrategies.put(nodePath, identityStrategy);
			return IdentityService.this;
		}
	}
}
