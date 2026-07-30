package org.skepsun.kototoro.core.parser.tvbox

internal object TVBoxVodMetadata {

	private val actorSeparator = Regex("""[,，、;\n\r]+""")
	private val actorCreditsLine = Regex(
		pattern = """(?im)^\s*(?:pornstars?|actors?|演员)\s*[:：]\s*(.+?)\s*$""",
	)

	fun parseAuthors(raw: String?): Set<String> {
		return raw
			.orEmpty()
			.split(actorSeparator)
			.map { it.trim() }
			.filterTo(linkedSetOf()) { it.isNotEmpty() }
	}

	fun findActorCredits(description: String?): String? {
		return actorCreditsLine.find(description.orEmpty())
			?.groupValues
			?.getOrNull(1)
			?.trim()
			?.takeIf { it.isNotEmpty() }
	}
}
