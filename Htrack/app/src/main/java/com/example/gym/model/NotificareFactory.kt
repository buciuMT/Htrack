package com.example.gym.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.gym.data.NotificareRequest
import java.time.LocalDate

object NotificareFactory {
    fun creeaza(
        tip: String,
        mesaj: String,
        data: String,
        citit: Boolean
    ): Notificare {
        val dataFormatata = data.take(10)
        return when (tip.lowercase()) {
            "abonare" -> NotificareAbonare(mesaj, dataFormatata, citit)
            "anulare" -> NotificareAnulareAbonament(mesaj, dataFormatata, citit)
            else -> NotificareGenerala(mesaj, dataFormatata, citit)
        }
    }
}

object NotificareRequestFactory {
    @RequiresApi(Build.VERSION_CODES.O)
    fun creeazaPentruAbonare(userId: Int, tipAbonament: String): NotificareRequest {
        return NotificareRequest(
            tip = "abonare",
            id_user = userId,
            mesaj = "Ai activat abonamentul: $tipAbonament",
            data = LocalDate.now().toString()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun creeazaPentruDezabonare(userId: Int): NotificareRequest {
        return NotificareRequest(
            tip = "anulare",
            id_user = userId,
            mesaj = "Abonamentul tău a fost dezactivat.",
            data = LocalDate.now().toString()
        )
    }
}

