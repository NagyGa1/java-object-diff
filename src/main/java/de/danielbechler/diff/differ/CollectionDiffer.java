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

package de.danielbechler.diff.differ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import de.danielbechler.diff.access.Accessor;
import de.danielbechler.diff.access.CollectionItemAccessor;
import de.danielbechler.diff.access.Instances;
import de.danielbechler.diff.comparison.ComparisonStrategy;
import de.danielbechler.diff.comparison.ComparisonStrategyResolver;
import de.danielbechler.diff.identity.IdentityStrategy;
import de.danielbechler.diff.identity.IdentityStrategyResolver;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.util.Assert;
import de.danielbechler.util.Collections;

/**
 * Used to find differences between {@link Collection Collections}.
 *
 * @author Daniel Bechler
 */
public final class CollectionDiffer implements Differ
{
	private final DifferDispatcher differDispatcher;
	private final ComparisonStrategyResolver comparisonStrategyResolver;
	private final IdentityStrategyResolver identityStrategyResolver;

	public CollectionDiffer(final DifferDispatcher differDispatcher,
			final ComparisonStrategyResolver comparisonStrategyResolver,
			final IdentityStrategyResolver identityStrategyResolver)
	{
		Assert.notNull(differDispatcher, "differDispatcher");
		this.differDispatcher = differDispatcher;

		Assert.notNull(comparisonStrategyResolver, "comparisonStrategyResolver");
		this.comparisonStrategyResolver = comparisonStrategyResolver;

		Assert.notNull(identityStrategyResolver, "identityStrategyResolver");
		this.identityStrategyResolver = identityStrategyResolver;
	}

	private static void compareUsingComparisonStrategy(final DiffNode collectionNode,
													   final Instances collectionInstances,
													   final ComparisonStrategy comparisonStrategy)
	{
		comparisonStrategy.compare(collectionNode, collectionInstances.getType(), collectionInstances.getWorking(Collection.class), collectionInstances.getBase(Collection.class));
	}

	private static DiffNode newNode(final DiffNode parentNode, final Instances collectionInstances)
	{
		final Accessor accessor = collectionInstances.getSourceAccessor();
		final Class<?> type = collectionInstances.getType();
		return new DiffNode(parentNode, accessor, type);
	}

	public boolean accepts(final Class<?> type)
	{
		return Collection.class.isAssignableFrom(type);
	}

	public final DiffNode compare(final DiffNode parentNode, final Instances collectionInstances)
	{
		final DiffNode collectionNode = newNode(parentNode, collectionInstances);
		final IdentityStrategy identityStrategy = identityStrategyResolver.resolveIdentityStrategy(collectionNode);
		if (collectionInstances.hasBeenAdded())
		{
			final Collection addedItems = collectionInstances.getWorking(Collection.class);
			compareItems(collectionNode, collectionInstances, addedItems, identityStrategy);
			collectionNode.setState(DiffNode.State.ADDED);
		}
		else if (collectionInstances.hasBeenRemoved())
		{
			final Collection<?> removedItems = collectionInstances.getBase(Collection.class);
			compareItems(collectionNode, collectionInstances, removedItems, identityStrategy);
			collectionNode.setState(DiffNode.State.REMOVED);
		}
		else if (collectionInstances.areSame())
		{
			collectionNode.setState(DiffNode.State.UNTOUCHED);
		}
		else
		{
			final ComparisonStrategy comparisonStrategy = comparisonStrategyResolver.resolveComparisonStrategy(collectionNode);
			if (comparisonStrategy == null)
			{
				compareInternally(collectionNode, collectionInstances,
						identityStrategy);
			}
			else
			{
				compareUsingComparisonStrategy(collectionNode, collectionInstances, comparisonStrategy);
			}
		}
		return collectionNode;
	}

	private void compareInternally(final DiffNode collectionNode,
			final Instances collectionInstances,
			final IdentityStrategy identityStrategy)
	{
		final Collection<?> working = collectionInstances.getWorking(Collection.class);
		final Collection<?> base = collectionInstances.getBase(Collection.class);

		final Collection<?> added = new LinkedList<Object>(working);
		final Collection<?> removed = new LinkedList<Object>(base);
		final Collection<?> known = new LinkedList<Object>(base);

		remove(added, base, identityStrategy);
		remove(removed, working, identityStrategy);
		remove(known, added, identityStrategy);
		remove(known, removed, identityStrategy);

		// TODO I am not sure why these are separate exactly? (NagyGa1)
		compareItems(collectionNode, collectionInstances, added, identityStrategy);
		compareItems(collectionNode, collectionInstances, removed, identityStrategy);
		compareItems(collectionNode, collectionInstances, known, identityStrategy);
	}

	private void remove(final Collection<?> from, final Collection<?> these, final IdentityStrategy identityStrategy) {
		final Iterator<?> iterator = from.iterator();
		while(iterator.hasNext()) {
			final Object _this = iterator.next();
			if(contains(these, _this, identityStrategy)) {
				iterator.remove();
			}
		}
	}

	private boolean contains(final Collection<?> these,
							 final Object _this,
							 final IdentityStrategy identityStrategy) {
		for (Object o : these) {
			if(identityStrategy.equals(_this, o)) {
				return true;
			}
		}

		return false;
	}

	private void compareItems(final DiffNode collectionNode,
							  final Instances collectionInstances,
							  final Iterable<?> items,
							  final IdentityStrategy identityStrategy)
	{
		for (final Object item : items)
		{
			final Accessor itemAccessor = new CollectionItemAccessor(item, identityStrategy);
			differDispatcher.dispatch(collectionNode, collectionInstances, itemAccessor);
		}
	}
}
