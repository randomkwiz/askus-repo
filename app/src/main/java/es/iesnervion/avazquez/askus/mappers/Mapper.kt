package es.iesnervion.avazquez.askus.mappers

abstract class Mapper<D, M> {
    abstract fun dtoToModel(dto: D): M
    abstract fun modelToDto(obj: M): D
    fun dtoListToModelList(objsListDTO: List<D>): List<M> {
        return objsListDTO.map { dtoToModel(it) }
    }

    fun modelListToDtoList(objsList: List<M>): List<D> {
        return objsList.map { modelToDto(it) }
    }
}