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

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.path.NodePath;

/**
 * Allows to configure the way objects identities are established when comparing
 * collections by CollectionDiffer.
 * <p />
 * Applies rules to subclasses / interface implementations.
 */
public interface IdentityConfigurer {
	/**
	 * @param nodePath of the collection to attach to.
	 */
	Of ofNode(NodePath nodePath);

	/**
	 * @param collectionElementType of the elements within the collection to attach to.
	 */
	Of ofType(Class<?> collectionElementType);

	/**
	 * @param type of the host class of the collection to attach to.
	 * @param propertyNames property name of the collection in the host class
	 */
	Of ofTypeAndProperty(Class<?> type, String... propertyNames);

	ObjectDifferBuilder and();

	interface Of {
		IdentityConfigurer toUse(IdentityStrategy identityStrategy);
	}

}
