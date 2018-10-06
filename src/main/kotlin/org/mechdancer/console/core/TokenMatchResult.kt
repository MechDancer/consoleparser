package org.mechdancer.console.core

import org.mechdancer.console.core.TokenMatchResult.*

enum class TokenMatchResult {
	Accepted, Rejected, RejectNext;

	operator fun component1(): Boolean =
		when (this) {
			Accepted   -> true
			Rejected   -> false
			RejectNext -> true
		}

	operator fun component2(): Boolean =
		when (this) {
			Accepted   -> true
			Rejected   -> false
			RejectNext -> false
		}
}

fun depends(what: Boolean) =
	if (what) Accepted else Rejected

fun nextDepends(what: Boolean) =
	if (what) Accepted else RejectNext
