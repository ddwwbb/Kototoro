package org.skepsun.kototoro.core.network.webview

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CloudFlareDetectionTest {

	@Test
	fun `waits for challenge page to navigate to target content`() = runTest {
		val states = ArrayDeque(listOf("\"wait\"", "\"wait\"", "\"ok\""))
		var polls = 0
		var waits = 0

		val ready = waitForCloudFlarePage(
			maxPolls = 5,
			readState = {
				polls++
				states.removeFirst()
			},
			waitForNextPoll = { waits++ },
		)

		assertTrue(ready)
		assertEquals(3, polls)
		assertEquals(2, waits)
	}

	@Test
	fun `stops when cloudflare explicitly blocks the page`() = runTest {
		var waits = 0

		val ready = waitForCloudFlarePage(
			maxPolls = 5,
			readState = { "\"error\"" },
			waitForNextPoll = { waits++ },
		)

		assertFalse(ready)
		assertEquals(0, waits)
	}
}
