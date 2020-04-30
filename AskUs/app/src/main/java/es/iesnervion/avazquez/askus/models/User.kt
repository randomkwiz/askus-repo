package es.iesnervion.avazquez.askus.models

data class User(
    var id: String? = "",
    var nickname: String,
    var email: String,
    var isModerador: Boolean? = false,
    var isBanned: Boolean? = false,
    var fechaInicioBaneo: String? = "",
    var duracionBaneoEnDias: Int? = 0,
    var fechaCreacionCuenta: String? = "",
    var fechaUltimoAcceso: String? = "",
    var isAdmin: Boolean? = false,
    var password: String
){
    constructor() :
            this("", "", "", false, false, "", 0, "",
            "", false, "")

    override fun toString(): String {
        return "UserModel(id='$id', nickname='$nickname', email='$email', isModerador=$isModerador, isBanned=$isBanned, fechaInicioBaneo='$fechaInicioBaneo', duracionBaneoEnDias=$duracionBaneoEnDias, fechaCreacionCuenta='$fechaCreacionCuenta', fechaUltimoAcceso='$fechaUltimoAcceso', isAdmin=$isAdmin, password='$password')"
    }
}

