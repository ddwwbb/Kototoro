package org.skepsun.kototoro.core.network.webview

/**
 * 返回值：
 * - "ok"：已进入真实页面
 * - "error"：被明确阻断
 * - "wait"：仍在等待或仍处于 Cloudflare challenge
 */
internal const val CF_STATE_JS = """
	(function(){
		try {
			var href = (document.location && document.location.href) || '';
			if (href === '' || href === 'about:blank') return 'wait';
			if (document.readyState !== 'interactive' && document.readyState !== 'complete') return 'wait';
			var t = (document.title || '').toLowerCase();
			if (t.indexOf('attention required') !== -1 || t.indexOf('access denied') !== -1) return 'error';
			if (t.indexOf('just a moment') !== -1 || t.indexOf('un instant') !== -1 ||
				t.indexOf('einen moment') !== -1 || t.indexOf('un momento') !== -1 ||
				t.indexOf('один момент') !== -1) return 'wait';
			if (document.querySelector('#challenge-running, #challenge-stage, #cf-challenge-running, .cf-browser-verification, #turnstile-wrapper, #cf-please-wait, script[src*="challenge-platform"]')) return 'wait';
			if (!document.body || document.body.children.length === 0) return 'wait';
			return 'ok';
		} catch (e) { return 'wait'; }
	})()
"""

internal suspend fun waitForCloudFlarePage(
	maxPolls: Int,
	readState: suspend () -> String?,
	waitForNextPoll: suspend () -> Unit,
	shouldContinue: () -> Boolean = { true },
): Boolean {
	require(maxPolls > 0) { "maxPolls must be positive" }
	repeat(maxPolls) { index ->
		if (!shouldContinue()) {
			return false
		}
		when (readState().normalizeCloudFlareState()) {
			"ok" -> return true
			"error" -> return false
		}
		if (index < maxPolls - 1) {
			waitForNextPoll()
		}
	}
	return false
}

private fun String?.normalizeCloudFlareState(): String {
	return this
		?.trim()
		?.removeSurrounding("\"")
		?.lowercase()
		.orEmpty()
}
