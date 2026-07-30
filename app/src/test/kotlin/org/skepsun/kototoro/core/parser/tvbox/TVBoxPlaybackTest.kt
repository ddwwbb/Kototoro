package org.skepsun.kototoro.core.parser.tvbox

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TVBoxPlaybackTest {

	@Test
	fun `marked playback page retains url and click selector`() {
		val marked = TVBoxPlayback.markHtmlPlaybackPage(
			value = "https://example.test/video/title/?token=a%20b",
			clickSelector = ".play-button[data-player='main']",
		)

		val request = TVBoxPlayback.parseHtmlSniffRequest(marked)

		assertEquals("https://example.test/video/title/?token=a%20b", request?.url)
		assertEquals(".play-button[data-player='main']", request?.clickSelector)
		assertTrue(TVBoxPlayback.looksLikeHtmlPlaybackPage(marked))
	}

	@Test
	fun `ordinary direct media url is not treated as playback page`() {
		assertFalse(TVBoxPlayback.looksLikeHtmlPlaybackPage("https://cdn.example.test/video/master.m3u8"))
	}
}
