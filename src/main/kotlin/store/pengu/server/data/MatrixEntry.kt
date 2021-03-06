package store.pengu.server.data

import org.jooq.types.ULong

data class MatrixEntry(
    val id: ULong,
    val row_number: String,
    val col_number: String,
    val cell_val: Int
)
