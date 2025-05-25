package com.example.gym.model

sealed class Notificare(
    open val mesaj: String,
    open val data: String,
    open val citit: Boolean
) {
    fun copy(citit: Boolean): Notificare = when (this) {
        is NotificareAbonare -> this.copy(citit = citit)
        is NotificareAnulareAbonament -> this.copy(citit = citit)
        is NotificareGenerala -> this.copy(citit = citit)
    }
}

data class NotificareAbonare(
    override val mesaj: String,
    override val data: String,
    override val citit: Boolean
) : Notificare(mesaj, data, citit)

data class NotificareAnulareAbonament(
    override val mesaj: String,
    override val data: String,
    override val citit: Boolean
) : Notificare(mesaj, data, citit)

data class NotificareGenerala(
    override val mesaj: String,
    override val data: String,
    override val citit: Boolean
) : Notificare(mesaj, data, citit)
