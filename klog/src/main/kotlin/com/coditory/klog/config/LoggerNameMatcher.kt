package com.coditory.klog.config

sealed interface LoggerNameMatcher {
    fun matches(name: String): Boolean

    companion object {
        fun matchAll(): LoggerNameMatcher {
            return LoggerNameAllMatcher
        }

        fun matchNone(): LoggerNameMatcher {
            return LoggerNameNoneMatcher
        }

        fun fromLoggerBase(baseName: String): LoggerNameMatcher {
            return LoggerNameBaseMatcher(baseName)
        }

        fun exactLogger(exactName: String): LoggerNameMatcher {
            return LoggerNameExactMatcher(exactName)
        }

        fun matchLogger(pattern: String): LoggerNameMatcher {
            return LoggerNamePatternMatcher(pattern)
        }
    }
}

data object LoggerNameAllMatcher : LoggerNameMatcher {
    override fun matches(name: String): Boolean {
        return true
    }
}

data object LoggerNameNoneMatcher : LoggerNameMatcher {
    override fun matches(name: String): Boolean {
        return false
    }
}

data class LoggerNameBaseMatcher(val baseName: String) : LoggerNameMatcher {
    private val baseNameWithDot = "$baseName."

    override fun matches(name: String): Boolean {
        return name.startsWith(baseNameWithDot) || name == baseName
    }
}

data class LoggerNameExactMatcher(val name: String) : LoggerNameMatcher {
    override fun matches(name: String): Boolean {
        return name == this.name
    }
}

data class LoggerNamePatternMatcher(val pattern: String) : LoggerNameMatcher {
    private val regex = Regex(pattern)

    override fun matches(name: String): Boolean {
        return regex.matches(name)
    }
}
