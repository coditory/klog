package com.coditory.klog.config

import com.coditory.klog.Level

data class LevelRange(
    override val start: Level,
    override val endInclusive: Level,
) : ClosedRange<Level> {
    companion object {
        fun allLevels(): LevelRange {
            return LevelRange(Level.TRACE, Level.FATAL)
        }

        fun noneLevel(): LevelRange {
            return LevelRange(Level.FATAL, Level.TRACE)
        }

        fun fromMinLevel(minLevel: Level): LevelRange {
            return LevelRange(minLevel, Level.FATAL)
        }

        fun toMaxLevel(maxLevel: Level): LevelRange {
            return LevelRange(Level.TRACE, maxLevel)
        }

        fun atLevel(level: Level): LevelRange {
            return LevelRange(level, level)
        }
    }
}
