package es.iesnervion.avazquez.askus.DTOs

class PaginHeader(val totalCount: Int,
    val pageSize: Int,
    val currentPage: Int,
    val totalPages: Int,
    val previousPage: String,
    val nextPage: String)