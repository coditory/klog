package com.coditory.klog.text.plain

internal object ConsoleColors {
    // Simplified detection by design to skip adding dependencies
    // XPC_SERVICE_NAME - used to force enable colors in intellij on macOS
    internal val ANSI_CONSOLE: Boolean =
        (System.console() != null && System.getenv("TERM") != null) ||
            System.getenv("XPC_SERVICE_NAME")
                ?.startsWith("application.com.jetbrains.intellij") == true

    // Reset
    const val RESET: String = "\u001b[0m"

    // Regular Colors
    const val BLACK: String = "\u001b[0;30m"
    const val RED: String = "\u001b[0;31m"
    const val GREEN: String = "\u001b[0;32m"
    const val YELLOW: String = "\u001b[0;33m"
    const val BLUE: String = "\u001b[0;34m"
    const val PURPLE: String = "\u001b[0;35m"
    const val CYAN: String = "\u001b[0;36m"
    const val WHITE: String = "\u001b[0;37m"

    // Bold
    const val BLACK_BOLD: String = "\u001b[1;30m"
    const val RED_BOLD: String = "\u001b[1;31m"
    const val GREEN_BOLD: String = "\u001b[1;32m"
    const val YELLOW_BOLD: String = "\u001b[1;33m"
    const val BLUE_BOLD: String = "\u001b[1;34m"
    const val PURPLE_BOLD: String = "\u001b[1;35m"
    const val CYAN_BOLD: String = "\u001b[1;36m"
    const val WHITE_BOLD: String = "\u001b[1;37m"

    // Underline
    const val BLACK_UNDERLINED: String = "\u001b[4;30m"
    const val RED_UNDERLINED: String = "\u001b[4;31m"
    const val GREEN_UNDERLINED: String = "\u001b[4;32m"
    const val YELLOW_UNDERLINED: String = "\u001b[4;33m"
    const val BLUE_UNDERLINED: String = "\u001b[4;34m"
    const val PURPLE_UNDERLINED: String = "\u001b[4;35m"
    const val CYAN_UNDERLINED: String = "\u001b[4;36m"
    const val WHITE_UNDERLINED: String = "\u001b[4;37m"

    // Background
    const val BLACK_BACKGROUND: String = "\u001b[40m"
    const val RED_BACKGROUND: String = "\u001b[41m"
    const val GREEN_BACKGROUND: String = "\u001b[42m"
    const val YELLOW_BACKGROUND: String = "\u001b[43m"
    const val BLUE_BACKGROUND: String = "\u001b[44m"
    const val PURPLE_BACKGROUND: String = "\u001b[45m"
    const val CYAN_BACKGROUND: String = "\u001b[46m"
    const val WHITE_BACKGROUND: String = "\u001b[47m"

    // High Intensity
    const val BLACK_BRIGHT: String = "\u001b[0;90m"
    const val RED_BRIGHT: String = "\u001b[0;91m"
    const val GREEN_BRIGHT: String = "\u001b[0;92m"
    const val YELLOW_BRIGHT: String = "\u001b[0;93m"
    const val BLUE_BRIGHT: String = "\u001b[0;94m"
    const val PURPLE_BRIGHT: String = "\u001b[0;95m"
    const val CYAN_BRIGHT: String = "\u001b[0;96m"
    const val WHITE_BRIGHT: String = "\u001b[0;97m"

    // Bold High Intensity
    const val BLACK_BOLD_BRIGHT: String = "\u001b[1;90m"
    const val RED_BOLD_BRIGHT: String = "\u001b[1;91m"
    const val GREEN_BOLD_BRIGHT: String = "\u001b[1;92m"
    const val YELLOW_BOLD_BRIGHT: String = "\u001b[1;93m"
    const val BLUE_BOLD_BRIGHT: String = "\u001b[1;94m"
    const val PURPLE_BOLD_BRIGHT: String = "\u001b[1;95m"
    const val CYAN_BOLD_BRIGHT: String = "\u001b[1;96m"
    const val WHITE_BOLD_BRIGHT: String = "\u001b[1;97m"

    // High Intensity backgrounds
    const val BLACK_BACKGROUND_BRIGHT: String = "\u001b[0;100m"
    const val RED_BACKGROUND_BRIGHT: String = "\u001b[0;101m"
    const val GREEN_BACKGROUND_BRIGHT: String = "\u001b[0;102m"
    const val YELLOW_BACKGROUND_BRIGHT: String = "\u001b[0;103m"
    const val BLUE_BACKGROUND_BRIGHT: String = "\u001b[0;104m"
    const val PURPLE_BACKGROUND_BRIGHT: String = "\u001b[0;105m"
    const val CYAN_BACKGROUND_BRIGHT: String = "\u001b[0;106m"
    const val WHITE_BACKGROUND_BRIGHT: String = "\u001b[0;107m"
}
