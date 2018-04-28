package net.nullsum.aufseher.api

import net.nullsum.aufseher.model.ColorMode


class POSTColorMode : ColorMode() {
    var strips: MutableList<String> = mutableListOf()
}
