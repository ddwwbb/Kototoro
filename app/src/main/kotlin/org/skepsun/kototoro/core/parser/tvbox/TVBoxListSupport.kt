package org.skepsun.kototoro.core.parser.tvbox

import org.skepsun.kototoro.parsers.model.ContentListFilter
import org.skepsun.kototoro.parsers.model.SortOrder

internal object TVBoxListSupport {

	fun searchTerm(filter: ContentListFilter?): String {
		return filter?.author?.trim().takeUnless { it.isNullOrEmpty() }
			?: filter?.query?.trim().orEmpty()
	}

	fun <T> sort(
		items: List<T>,
		order: SortOrder?,
		query: String,
		titleOf: (T) -> String,
	): List<T> {
		return when (order) {
			SortOrder.ALPHABETICAL -> items.sortedBy { titleOf(it).lowercase() }
			SortOrder.ALPHABETICAL_DESC -> items.sortedByDescending { titleOf(it).lowercase() }
			SortOrder.RELEVANCE -> {
				val normalizedQuery = query.trim()
					.takeIf { it.isNotEmpty() }
					?: return items
				items.sortedBy { item ->
					val title = titleOf(item)
					when {
						title.equals(normalizedQuery, ignoreCase = true) -> 0
						title.startsWith(normalizedQuery, ignoreCase = true) -> 1
						title.contains(normalizedQuery, ignoreCase = true) -> 2
						else -> 3
					}
				}
			}
			else -> items
		}
	}
}
