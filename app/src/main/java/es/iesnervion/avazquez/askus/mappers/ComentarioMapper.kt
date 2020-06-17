package es.iesnervion.avazquez.askus.mappers

import es.iesnervion.avazquez.askus.DTOs.ComentarioDTO
import es.iesnervion.avazquez.askus.models.Comentario

class ComentarioMapper : Mapper<ComentarioDTO, Comentario>() {
    override fun dtoToModel(dto: ComentarioDTO): Comentario {
        return Comentario(dto.id, dto.fechaPublicacion, dto.idComentarioAlQueResponde,
            dto.idUsuario, dto.idPublicacion, dto.isBanned, dto.isBorrado, dto.texto, dto.titulo)
    }

    override fun modelToDto(obj: Comentario): ComentarioDTO {
        return ComentarioDTO(obj.id, obj.fechaPublicacion, obj.idComentarioAlQueResponde,
            obj.idUsuario, obj.idPublicacion, obj.isBanned, obj.isBorrado, obj.texto, obj.titulo)
    }
}