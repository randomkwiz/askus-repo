package es.iesnervion.avazquez.askus.mappers

import es.iesnervion.avazquez.askus.DTOs.UserDTO
import es.iesnervion.avazquez.askus.models.User

class UserMapper() : Mapper<UserDTO, User>() {

    override fun dtoToModel(dto: UserDTO): User {
        return User(
            dto.id,
            dto.nickname,
            dto.email?:"",
            dto.isModerador?:false,
            dto.isBanned?:false,
            dto.fechaInicioBaneo?:"",
            dto.duracionBaneoEnDias?:0,
            dto.fechaCreacionCuenta?:"",
            dto.fechaUltimoAcceso?:"",
            dto.isAdmin?:false,
            password = ""
        )
    }

    override fun modelToDto(obj: User): UserDTO {
        return UserDTO(
            obj.id ?: "",
            obj.nickname,
            obj.email,
            obj.isModerador,
            obj.isBanned,
            obj.fechaInicioBaneo,
            obj.duracionBaneoEnDias,
            obj.fechaCreacionCuenta,
            obj.fechaUltimoAcceso,
            obj.isAdmin
        )
    }
}