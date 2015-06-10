package de.danielbechler.diff.identity;

import de.danielbechler.util.Objects;

/**
 * Default implementation that uses Object.equals.
 */
public class EqualsIdentityStrategy implements IdentityStrategy {

	public boolean equals(final Object _this, final Object o) {
		return Objects.isEqual(_this, o);
	}

	public int hashCode(final Object _this) {
		return _this.hashCode();
	}
}
