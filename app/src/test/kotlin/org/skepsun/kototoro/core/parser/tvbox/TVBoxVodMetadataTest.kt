package org.skepsun.kototoro.core.parser.tvbox

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.skepsun.kototoro.parsers.model.ContentListFilter
import org.skepsun.kototoro.parsers.model.SortOrder

class TVBoxVodMetadataTest {

	@Test
	fun `actor credits become distinct names`() {
		TVBoxVodMetadata.parseAuthors("Arisu Hoshi, Mei Washio、Arisu Hoshi")
			.shouldBe(linkedSetOf("Arisu Hoshi", "Mei Washio"))
	}

	@Test
	fun `actor credits are discovered in detail text`() {
		val description = """
			Release date: 2025-01-02
			Pornstars: Arisu Hoshi, Mei Washio
			Studio: Example
		""".trimIndent()

		TVBoxVodMetadata.findActorCredits(description) shouldBe "Arisu Hoshi, Mei Washio"
	}

	@Test
	fun `actor filter takes precedence over ordinary query`() {
		val filter = ContentListFilter(query = "unrelated title", author = "Arisu Hoshi")

		TVBoxListSupport.searchTerm(filter) shouldBe "Arisu Hoshi"
	}

	@Test
	fun `alphabetical sorting orders the loaded actor page`() {
		val titles = listOf("Zeta", "alpha", "Beta")

		TVBoxListSupport.sort(titles, SortOrder.ALPHABETICAL, "") { it } shouldBe
			listOf("alpha", "Beta", "Zeta")
	}
}
