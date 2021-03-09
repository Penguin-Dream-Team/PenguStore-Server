package store.pengu.server.utils

class StringSetBuilder {
    private val set = mutableSetOf<String>()

    fun build(): Set<String> {
        return set
    }

    operator fun String.unaryPlus() {
        set += this
    }

    operator fun String.unaryMinus() {
        set -= this
    }
}