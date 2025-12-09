package com.aburustum.horseshoecrab.entity.custom

enum class HorseshoeCrabVariant(val id: Int) {
    MALE(0),
    FEMALE(1),
    ;

    companion object {
        private val map = entries.associateBy(HorseshoeCrabVariant::id)
        fun byId(type: Int): HorseshoeCrabVariant = map[type]!!
    }
}
