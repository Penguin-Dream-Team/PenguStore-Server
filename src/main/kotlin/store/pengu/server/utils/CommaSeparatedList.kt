package store.pengu.server.utils

data class CommaSeparatedList(val elems: List<String>) {
    override fun toString(): String {
        return elems.joinToString(",")
    }
}

fun emptyCommaSeparatedList() = CommaSeparatedList(emptyList())