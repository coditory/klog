package com.coditory.klog.text.plain

internal data class Padding(
    val direction: Direction = Direction.LEFT,
    val pad: Char = ' ',
) {
    fun left(): Boolean {
        return direction == Direction.LEFT
    }

    fun right(): Boolean {
        return direction == Direction.RIGHT
    }

    companion object {
        fun left(pad: Char = ' '): Padding {
            return Padding(Direction.LEFT, pad)
        }

        fun right(pad: Char = ' '): Padding {
            return Padding(Direction.RIGHT, pad)
        }
    }

    internal enum class Direction {
        LEFT,
        RIGHT,
    }
}
