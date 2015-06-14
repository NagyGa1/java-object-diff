package de.danielbechler.diff.identity;

/**
 * Allows to configure the way objects identities are established when comparing
 * collections by CollectionDiffer.
 */
public interface IdentityStrategy {

	/**
	 *
	 * @param _this
	 *            never null
	 * @param o
	 * @return
	 */
	boolean equals(Object _this, Object o);

	/**
	 *
	 * @param _this
	 *            never null
	 * @return
	 */
	int hashCode(Object _this);

}
