package store.pengu.server.data

import org.jooq.types.ULong

data class MatrixEntry(
    val row_number: ULong,
    val col_number: ULong,
    val cell_val: Int
)
