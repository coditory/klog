package com.coditory.klog

enum class Level {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL,
    ;

    companion object {
        private val ALL_LEVELS = entries
        private val TRACE_LEVELS = ALL_LEVELS
        private val DEBUG_LEVELS = listOf(DEBUG, INFO, WARN, ERROR, FATAL)
        private val INFO_LEVELS = listOf(INFO, WARN, ERROR, FATAL)
        private val WARN_LEVELS = listOf(WARN, ERROR, FATAL)
        private val ERROR_LEVELS = listOf(ERROR, FATAL)
        private val FATAL_LEVELS = listOf(FATAL)
        internal val LONGEST_LEVEL_NAME = ALL_LEVELS.maxOfOrNull { it.name.length } ?: 0

        fun levels(): List<Level> {
            return ALL_LEVELS
        }

        internal fun higherLevelsOrEqual(level: Level): List<Level> {
            return when (level) {
                TRACE -> TRACE_LEVELS
                DEBUG -> DEBUG_LEVELS
                INFO -> INFO_LEVELS
                WARN -> WARN_LEVELS
                ERROR -> ERROR_LEVELS
                FATAL -> FATAL_LEVELS
            }
        }
    }
}
