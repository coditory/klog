package com.coditory.klog.config

import com.coditory.klog.Level

data class LogFilter(
    val range: LevelRange,
    val nameMatcher: LoggerNameMatcher,
) {
    fun matches(
        level: Level,
        logger: String,
    ): Boolean {
        return range.contains(level) && nameMatcher.matches(logger)
    }

    companion object {
        fun matchAll() =
            LogFilter(
                range = LevelRange.allLevels(),
                nameMatcher = LoggerNameMatcher.matchAll(),
            )

        fun matchNone() =
            LogFilter(
                range = LevelRange.allLevels(),
                nameMatcher = LoggerNameMatcher.matchAll(),
            )

        fun filterByMinLevel(level: Level) =
            LogFilter(
                range = LevelRange.fromMinLevel(level),
                nameMatcher = LoggerNameMatcher.matchAll(),
            )

        fun filterByBaseLoggerName(name: String): LogFilter =
            LogFilter(
                range = LevelRange.allLevels(),
                nameMatcher = LoggerNameMatcher.fromLoggerBase(name),
            )

        fun filterByNameAndLevel(
            range: LevelRange = LevelRange.allLevels(),
            nameMatcher: LoggerNameMatcher = LoggerNameMatcher.matchAll(),
        ) = LogFilter(range, nameMatcher)
    }
}
