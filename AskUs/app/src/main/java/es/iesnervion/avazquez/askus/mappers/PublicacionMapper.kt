package es.iesnervion.avazquez.askus.mappers

import es.iesnervion.avazquez.askus.DTOs.PublicacionDTO
import es.iesnervion.avazquez.askus.models.Publicacion

class PublicacionMapper() : Mapper<PublicacionDTO, Publicacion>() {
    override fun dtoToModel(dto: PublicacionDTO): Publicacion {
       return Publicacion(dto.id,
       dto.fechaCreacion,
       dto.fechaPublicacion,
       dto.idAutor,
       dto.isBorrado,
       dto.isPrivada,
       dto.isPublicado,
       dto.texto,
       dto.titulo)
    }
    override fun modelToDto(obj: Publicacion): PublicacionDTO {
        return PublicacionDTO(obj.id,
            obj.fechaCreacion,
            obj.fechaPublicacion,
            obj.idAutor,
            obj.isBorrado,
            obj.isPrivada,
            obj.isPublicado,
            obj.texto,
            obj.titulo)
    }
}