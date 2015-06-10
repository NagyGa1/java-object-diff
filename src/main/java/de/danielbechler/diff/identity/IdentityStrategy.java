package de.danielbechler.diff.identity;

public interface IdentityStrategy {

	/**
	 *
	 * @param _this never null
	 * @param o
	 * @return
	 */
	public boolean equals(final Object _this, final Object o);

	/**
	 *
	 * @param _this never null
	 * @return
	 */
	public int hashCode(final Object _this);

}
