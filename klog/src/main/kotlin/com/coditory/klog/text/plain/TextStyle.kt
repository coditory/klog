package com.coditory.klog.text.plain

data class TextStyle(
    val prefix: String,
    val postfix: String,
) {
    companion object {
        fun empty() = TextStyle("", "")

        // Colors
        fun black() = TextStyle(ConsoleColors.BLACK, ConsoleColors.RESET)

        fun red() = TextStyle(ConsoleColors.RED, ConsoleColors.RESET)

        fun green() = TextStyle(ConsoleColors.GREEN, ConsoleColors.RESET)

        fun yellow() = TextStyle(ConsoleColors.YELLOW, ConsoleColors.RESET)

        fun blue() = TextStyle(ConsoleColors.BLUE, ConsoleColors.RESET)

        fun purple() = TextStyle(ConsoleColors.PURPLE, ConsoleColors.RESET)

        fun cyan() = TextStyle(ConsoleColors.CYAN, ConsoleColors.RESET)

        fun white() = TextStyle(ConsoleColors.WHITE, ConsoleColors.RESET)

        // Bold
        fun blackBold() = TextStyle(ConsoleColors.BLACK_BOLD, ConsoleColors.RESET)

        fun redBold() = TextStyle(ConsoleColors.RED_BOLD, ConsoleColors.RESET)

        fun greenBold() = TextStyle(ConsoleColors.GREEN_BOLD, ConsoleColors.RESET)

        fun yellowBold() = TextStyle(ConsoleColors.YELLOW_BOLD, ConsoleColors.RESET)

        fun blueBold() = TextStyle(ConsoleColors.BLUE_BOLD, ConsoleColors.RESET)

        fun purpleBold() = TextStyle(ConsoleColors.PURPLE_BOLD, ConsoleColors.RESET)

        fun cyanBold() = TextStyle(ConsoleColors.CYAN_BOLD, ConsoleColors.RESET)

        fun whiteBold() = TextStyle(ConsoleColors.WHITE_BOLD, ConsoleColors.RESET)

        // Underline
        fun blackUnderlined() = TextStyle(ConsoleColors.BLACK_UNDERLINED, ConsoleColors.RESET)

        fun redUnderlined() = TextStyle(ConsoleColors.RED_UNDERLINED, ConsoleColors.RESET)

        fun greenUnderlined() = TextStyle(ConsoleColors.GREEN_UNDERLINED, ConsoleColors.RESET)

        fun yellowUnderlined() = TextStyle(ConsoleColors.YELLOW_UNDERLINED, ConsoleColors.RESET)

        fun blueUnderlined() = TextStyle(ConsoleColors.BLUE_UNDERLINED, ConsoleColors.RESET)

        fun purpleUnderlined() = TextStyle(ConsoleColors.PURPLE_UNDERLINED, ConsoleColors.RESET)

        fun cyanUnderlined() = TextStyle(ConsoleColors.CYAN_UNDERLINED, ConsoleColors.RESET)

        fun whiteUnderlined() = TextStyle(ConsoleColors.WHITE_UNDERLINED, ConsoleColors.RESET)

        // Background
        fun blackBackground() = TextStyle(ConsoleColors.BLACK_BACKGROUND, ConsoleColors.RESET)

        fun redBackground() = TextStyle(ConsoleColors.RED_BACKGROUND, ConsoleColors.RESET)

        fun greenBackground() = TextStyle(ConsoleColors.GREEN_BACKGROUND, ConsoleColors.RESET)

        fun yellowBackground() = TextStyle(ConsoleColors.YELLOW_BACKGROUND, ConsoleColors.RESET)

        fun blueBackground() = TextStyle(ConsoleColors.BLUE_BACKGROUND, ConsoleColors.RESET)

        fun purpleBackground() = TextStyle(ConsoleColors.PURPLE_BACKGROUND, ConsoleColors.RESET)

        fun cyanBackground() = TextStyle(ConsoleColors.CYAN_BACKGROUND, ConsoleColors.RESET)

        fun whiteBackground() = TextStyle(ConsoleColors.WHITE_BACKGROUND, ConsoleColors.RESET)

        // High Intensity
        fun blackBright() = TextStyle(ConsoleColors.BLACK_BRIGHT, ConsoleColors.RESET)

        fun redBright() = TextStyle(ConsoleColors.RED_BRIGHT, ConsoleColors.RESET)

        fun greenBright() = TextStyle(ConsoleColors.GREEN_BRIGHT, ConsoleColors.RESET)

        fun yellowBright() = TextStyle(ConsoleColors.YELLOW_BRIGHT, ConsoleColors.RESET)

        fun blueBright() = TextStyle(ConsoleColors.BLUE_BRIGHT, ConsoleColors.RESET)

        fun purpleBright() = TextStyle(ConsoleColors.PURPLE_BRIGHT, ConsoleColors.RESET)

        fun cyanBright() = TextStyle(ConsoleColors.CYAN_BRIGHT, ConsoleColors.RESET)

        fun whiteBright() = TextStyle(ConsoleColors.WHITE_BRIGHT, ConsoleColors.RESET)

        // Bold High Intensity
        fun blackBoldBright() = TextStyle(ConsoleColors.BLACK_BOLD_BRIGHT, ConsoleColors.RESET)

        fun redBoldBright() = TextStyle(ConsoleColors.RED_BOLD_BRIGHT, ConsoleColors.RESET)

        fun greenBoldBright() = TextStyle(ConsoleColors.GREEN_BOLD_BRIGHT, ConsoleColors.RESET)

        fun yellowBoldBright() = TextStyle(ConsoleColors.YELLOW_BOLD_BRIGHT, ConsoleColors.RESET)

        fun blueBoldBright() = TextStyle(ConsoleColors.BLUE_BOLD_BRIGHT, ConsoleColors.RESET)

        fun purpleBoldBright() = TextStyle(ConsoleColors.PURPLE_BOLD_BRIGHT, ConsoleColors.RESET)

        fun cyanBoldBright() = TextStyle(ConsoleColors.CYAN_BOLD_BRIGHT, ConsoleColors.RESET)

        fun whiteBoldBright() = TextStyle(ConsoleColors.WHITE_BOLD_BRIGHT, ConsoleColors.RESET)

        // High Intensity backgrounds
        fun blackBackgroundBright() = TextStyle(ConsoleColors.BLACK_BACKGROUND_BRIGHT, ConsoleColors.RESET)

        fun redBackgroundBright() = TextStyle(ConsoleColors.RED_BACKGROUND_BRIGHT, ConsoleColors.RESET)

        fun greenBackgroundBright() = TextStyle(ConsoleColors.GREEN_BACKGROUND_BRIGHT, ConsoleColors.RESET)

        fun yellowBackgroundBright() = TextStyle(ConsoleColors.YELLOW_BACKGROUND_BRIGHT, ConsoleColors.RESET)

        fun blueBackgroundBright() = TextStyle(ConsoleColors.BLUE_BACKGROUND_BRIGHT, ConsoleColors.RESET)

        fun purpleBackgroundBright() = TextStyle(ConsoleColors.PURPLE_BACKGROUND_BRIGHT, ConsoleColors.RESET)

        fun cyanBackgroundBright() = TextStyle(ConsoleColors.CYAN_BACKGROUND_BRIGHT, ConsoleColors.RESET)

        fun whiteBackgroundBright() = TextStyle(ConsoleColors.WHITE_BACKGROUND_BRIGHT, ConsoleColors.RESET)
    }
}
