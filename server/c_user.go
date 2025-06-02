package main

import (
	"fmt"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
)

func (ctx *CContext) GetUsers(c *gin.Context) {
	var users []User
	if err := ctx.DB.Where("TIP_USER = ?", "USER").Find(&users).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Database error"})
		return
	}
	c.JSON(http.StatusOK, users)
}

func (ctx *CContext) GetTrainers(c *gin.Context) {
	var users []User
	if err := ctx.DB.Where("TIP_USER = ?", "TRAINER").Find(&users).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Database error"})
		return
	}
	c.JSON(http.StatusOK, users)
}

func (ctx *CContext) TransformUserToTrainer(c *gin.Context) {
	userId := c.Param("id")

	var user User
	if err := ctx.DB.First(&user, "id_user = ?", userId).Error; err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Utilizatorul nu a fost găsit"})
		return
	}

	if err := ctx.DB.Model(&user).Update("tip_user", "TRAINER").Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la actualizarea tipului de utilizator"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"message": "Utilizator transformat în trainer",
	})
}

func (ctx *CContext) GetUsersFaraAntrenor(c *gin.Context) {
	var users []User
	if err := ctx.DB.
		Where("tip_user = ? AND antrenor_id IS NULL", "USER").
		Find(&users).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare DB"})
		return
	}
	c.JSON(http.StatusOK, users)
}

func (ctx *CContext) SetAntrenorLaUser(c *gin.Context) {
	userIDStr := c.Param("id")
	trainerIDStr := c.Param("trainerId")

	userID, err := strconv.ParseInt(userIDStr, 10, 64)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID utilizator invalid"})
		return
	}

	trainerID, err := strconv.ParseInt(trainerIDStr, 10, 64)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID antrenor invalid"})
		return
	}

	if err := ctx.DB.Model(&User{}).
		Where("id_user = ? AND tip_user = ?", userID, "USER").
		Update("antrenor_id", trainerID).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare actualizare"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Antrenor atribuit cu succes"})
}

func (ctx *CContext) GetUsersByTrainer(c *gin.Context) {
	trainerIDStr := c.Param("trainerId")

	trainerID, err := strconv.Atoi(trainerIDStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID antrenor invalid"})
		return
	}

	var users []User
	if err := ctx.DB.
		Where("tip_user = ? AND antrenor_id = ?", "USER", trainerID).
		Find(&users).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare DB"})
		return
	}

	c.JSON(http.StatusOK, users)
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

func (ctx *CContext) GetUsersForTrainer(c *gin.Context) {
	trainerIdStr := c.Param("trainerId")
	trainerId, err := strconv.Atoi(trainerIdStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID invalid"})
		return
	}

	var users []struct {
		IDUser   int    `json:"id_user"`
		Username string `json:"username"`
	}

	err = ctx.DB.
		Table("users").
		Select("id_user, username").
		Where("antrenor_id = ?", trainerId).
		Scan(&users).Error

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la interogare"})
		return
	}

	c.JSON(http.StatusOK, users)
}



