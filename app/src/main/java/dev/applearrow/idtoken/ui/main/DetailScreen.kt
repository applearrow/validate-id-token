package dev.applearrow.idtoken.ui.main

enum class DetailScreen {
    DECODED_TOKEN,
    OTHER_SCREEN;

    companion object {
        fun fromString(str: String): DetailScreen? {
            return when (str) {
                DECODED_TOKEN.name -> DECODED_TOKEN
                OTHER_SCREEN.name -> OTHER_SCREEN
                else -> null
            }
        }
    }
}