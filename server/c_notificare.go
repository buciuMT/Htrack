package main

import (
	"fmt"
	"log"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func SaveNotificare(db *gorm.DB, idUser int, mesaj string, tip string) error {
	notif := Notificare{
		IDUser: idUser,
		Mesaj:  mesaj,
		Tip:    tip,
		Data:   time.Now(),
		Citit:  false,
	}
	return db.Create(&notif).Error
}

type NotificationSaver struct {
	DB *gorm.DB
}

func (n NotificationSaver) OnMessageSent(msg Message) {
	var conv struct {
		IDUser    int
		IDTrainer int
	}

	// Caută participanții la conversație
	err := n.DB.Table("conversations").
		Select("id_user, id_trainer").
		Where("id_conversation = ?", msg.IDConversation).
		Scan(&conv).Error
	if err != nil {
		log.Printf("Eroare la extragerea conversației: %v", err)
		return
	}

	var idReceiver int
	if msg.IDSender == conv.IDTrainer {
		idReceiver = conv.IDUser
	} else {
		idReceiver = conv.IDTrainer
	}

	// Salvează notificarea pentru destinatar
	err = SaveNotificare(n.DB, idReceiver, "Ai un mesaj nou", "chat")
	if err != nil {
		log.Printf("Eroare la salvarea notificării în observer: %v", err)
	}
}

func (ctx *CContext) AddNotificare(c *gin.Context) {
	var req struct {
		IDUser int    `json:"id_user"`
		Mesaj  string `json:"mesaj"`
		Tip    string `json:"tip"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "JSON invalid"})
		return
	}

	notif := Notificare{
		IDUser: req.IDUser,
		Mesaj:  req.Mesaj,
		Tip:    req.Tip,
		Data:   time.Now(),
		Citit:  false,
	}

	if err := ctx.DB.Create(&notif).Error; err != nil {
		fmt.Println("DB ERROR:", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare DB la creare notificare"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"message":    "Notificare adăugată",
		"notificare": notif,
	})
}

func (ctx *CContext) GetNotificariUser(c *gin.Context) {
	idUserStr := c.Param("id_user")
	idUser, err := strconv.Atoi(idUserStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID invalid"})
		return
	}

	var notificari []Notificare
	if err := ctx.DB.
		Where("ID_USER = ?", idUser).
		Order("DATA DESC").
		Find(&notificari).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la interogare notificări"})
		return
	}

	c.JSON(http.StatusOK, notificari)
}

func (ctx *CContext) MarcheazaNotificariCitite(c *gin.Context) {
	idUserStr := c.Param("id_user")
	idUser, err := strconv.Atoi(idUserStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID invalid"})
		return
	}

	if err := ctx.DB.
		Model(&Notificare{}).
		Where("id_user = ? AND citit = false", idUser).
		Update("citit", true).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare DB"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Toate notificările au fost marcate ca citite"})
}

func (ctx *CContext) GetTrainerForUser(c *gin.Context) {
	idUserStr := c.Param("id_user")
	idUser, err := strconv.Atoi(idUserStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID invalid"})
		return
	}

	var idTrainer *int
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
		})
		return
	}

	if idTrainer == nil {
		c.JSON(http.StatusOK, gin.H{"antrenor_id": nil})
	} else {
		c.JSON(http.StatusOK, gin.H{"antrenor_id": *idTrainer})
	}
}
