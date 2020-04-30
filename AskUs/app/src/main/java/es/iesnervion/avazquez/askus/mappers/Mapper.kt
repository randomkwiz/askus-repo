package es.iesnervion.avazquez.askus.mappers

abstract class Mapper<D, M> {

    abstract fun dtoToModel(dto: D): M

    abstract fun modelToDto(obj: M): D

    fun dtoListToModelList(objsListDTO: MutableList<D>): MutableList<M> {
        var objList = mutableListOf<M>()
        for (i in objsListDTO.indices) {
            dtoToModel(objsListDTO[i]).let { objList.add(it) }
        }
        return objList
    }

    fun modelListToDtoList(objsList: MutableList<M>): MutableList<D> {
        var objsDTOS = mutableListOf<D>()
        for (i in objsDTOS.indices) {
            modelToDto(objsList[i])?.let { objsDTOS.add(it) }
        }
        return objsDTOS
    }
}