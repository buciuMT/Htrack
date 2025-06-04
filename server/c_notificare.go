package main

import (
	"fmt"      // Pentru functii de formatare I/O.
	"log"      // Pentru logarea mesajelor.
	"net/http" // Pentru functionalitati HTTP.
	"strconv"  // Pentru conversii string la numere.
	"time"     // Pentru lucrul cu timpul.

	"github.com/gin-gonic/gin" // Framework web Gin.
	"gorm.io/gorm"             // Libraria GORM ORM.
)

// SaveNotificare salveaza o notificare noua in baza de date.
func SaveNotificare(db *gorm.DB, idUser int, mesaj string, tip string) error {
	notif := Notificare{
		IDUser: idUser,     // ID-ul utilizatorului.
		Mesaj:  mesaj,      // Mesajul notificarii.
		Tip:    tip,        // Tipul notificarii (ex: "chat").
		Data:   time.Now(), // Data si ora curenta.
		Citit:  false,      // Notificare necitita initial.
	}
	return db.Create(&notif).Error // Salveaza notificarea.
}

// NotificationSaver gestioneaza salvarea notificarilor.
type NotificationSaver struct {
	DB *gorm.DB // Conexiunea la baza de date.
}

// OnMessageSent este apelata cand un mesaj este trimis.
// Salveaza o notificare pentru destinatar.
func (n NotificationSaver) OnMessageSent(msg Message) {
	var conv struct {
		IDUser    int
		IDTrainer int
	}

	// Cauta participantii la conversatie.
	err := n.DB.Table("conversations").
		Select("id_user, id_trainer").
		Where("id_conversation = ?", msg.IDConversation).
		Scan(&conv).Error
	if err != nil {
		log.Printf("Eroare la extragerea conversatiei: %v", err) // Logheaza eroarea.
		return
	}

	var idReceiver int
	// Determina destinatarul mesajului.
	if msg.IDSender == conv.IDTrainer {
		idReceiver = conv.IDUser
	} else {
		idReceiver = conv.IDTrainer
	}

	// Salveaza notificarea pentru destinatar.
	err = SaveNotificare(n.DB, idReceiver, "Ai un mesaj nou", "chat")
	if err != nil {
		log.Printf("Eroare la salvarea notificarii in observer: %v", err) // Logheaza eroarea.
	}
}

// AddNotificare adauga o notificare noua prin API.
func (ctx *CContext) AddNotificare(c *gin.Context) {
	var req struct {
		IDUser int    `json:"id_user"`
		Mesaj  string `json:"mesaj"`
		Tip    string `json:"tip"`
	}
	// Parseaza corpul cererii JSON.
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "JSON invalid"}) // Eroare JSON.
		return
	}

	notif := Notificare{
		IDUser: req.IDUser, // ID-ul utilizatorului.
		Mesaj:  req.Mesaj,  // Mesajul.
		Tip:    req.Tip,    // Tipul.
		Data:   time.Now(), // Data curenta.
		Citit:  false,      // Necitita.
	}

	// Salveaza notificarea in DB.
	if err := ctx.DB.Create(&notif).Error; err != nil {
		fmt.Println("DB ERROR:", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare DB la creare notificare"}) // Eroare DB.
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"message":    "Notificare adaugata", // Mesaj de succes.
		"notificare": notif,                 // Notificarea creata.
	})
}

// GetNotificariUser preia notificarile pentru un anumit utilizator.
func (ctx *CContext) GetNotificariUser(c *gin.Context) {
	idUserStr := c.Param("id_user")        // Extrage ID-ul utilizatorului din URL.
	idUser, err := strconv.Atoi(idUserStr) // Convertește ID-ul la int.
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID invalid"}) // Eroare ID.
		return
	}

	var notificari []Notificare
	// Cauta notificarile in DB.
	if err := ctx.DB.
		Where("ID_USER = ?", idUser).
		Order("DATA DESC").
		Find(&notificari).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la interogare notificari"}) // Eroare DB.
		return
	}

	c.JSON(http.StatusOK, notificari) // Returneaza notificarile.
}

// MarcheazaNotificariCitite marcheaza toate notificarile unui utilizator ca fiind citite.
func (ctx *CContext) MarcheazaNotificariCitite(c *gin.Context) {
	idUserStr := c.Param("id_user")        // Extrage ID-ul utilizatorului.
	idUser, err := strconv.Atoi(idUserStr) // Convertește ID-ul.
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID invalid"}) // Eroare ID.
		return
	}

	// Actualizeaza notificarile ca citite.
	if err := ctx.DB.
		Model(&Notificare{}).
		Where("id_user = ? AND citit = false", idUser).
		Update("citit", true).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare DB"}) // Eroare DB.
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Toate notificarile au fost marcate ca citite"}) // Succes.
}

// GetTrainerForUser preia ID-ul antrenorului pentru un utilizator.
func (ctx *CContext) GetTrainerForUser(c *gin.Context) {
	idUserStr := c.Param("id_user")        // Extrage ID-ul utilizatorului.
	idUser, err := strconv.Atoi(idUserStr) // Convertește ID-ul.
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID invalid"}) // Eroare ID.
		return
	}

	var idTrainer *int
	// Cauta antrenorul asociat utilizatorului.
	err = ctx.DB.
		Table("users").
		Select("antrenor_id").
		Where("id_user = ?", idUser).
		Limit(1).
		Scan(&idTrainer).Error
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error":       "Eroare DB",
			"description": err.Error(),
		}) // Eroare DB.
		return
	}

	// Returneaza ID-ul antrenorului.
	if idTrainer == nil {
		c.JSON(http.StatusOK, gin.H{"antrenor_id": nil}) // Nu exista antrenor.
	} else {
		c.JSON(http.StatusOK, gin.H{"antrenor_id": *idTrainer}) // Antrenor gasit.
	}
}
