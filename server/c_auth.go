package main

import (
	"fmt"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
)

type LoginRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
	IDUser   int    `json:"id_user"`
}
type RegisterRequest struct {
	Email    string `json:"email"`
	Username string `json:"username"`
	Password string `json:"password"`
}

func (ctx *CContext) C_Register(c *gin.Context) {
	var req RegisterRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{
			"success": false,
			"message": "INVALID JSON",
		})
		return
	}
	hashed, err := bcrypt.GenerateFromPassword([]byte(req.Password), 0)
	if err != nil {
		c.JSON(406, gin.H{
			"message": err.Error(),
		})
		return
	}
	user := User{
		Email:    string(req.Email),
		Username: string(req.Username),
		Parola:   string(hashed),
		Tip_user: "USER",
	}
	res := ctx.DB.Create(&user)
	if res.Error != nil {
		c.JSON(401, gin.H{
			"message": res.Error.Error(),
		})
		return
	}
	c.JSON(200, gin.H{
		"message": "success",
	})
}

func (ctx *CContext) C_Login(c *gin.Context) {
	var req LoginRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{
			"success": false,
			"message": "INVALID JSON",
		})
		return
	}

	var user User
	res := ctx.DB.Where("username = ?", req.Username).First(&user)

	fmt.Println("Error:", res.Error)
	fmt.Printf("User: %+v\n", user)

	if res.Statement != nil {
		fmt.Println("SQL:", res.Statement.SQL.String())
	} else {
		fmt.Println("SQL statement nu a fost generat.")
	}

	if res.Error != nil {
		fmt.Println("User not found:", res.Error)
		c.JSON(404, gin.H{
			"success": false,
			"message": "User not found",
		})
		return
	}

	if bcrypt.CompareHashAndPassword([]byte(user.Parola), []byte(req.Password)) != nil {
		c.JSON(401, gin.H{
			"success": false,
			"message": "Wrong password",
		})
		return
	}

	session := GenerateNewSession(ctx.DB, user.Id_user)
	c.JSON(200, gin.H{
		"success":  true,
		"message":  session,
		"tip_user": user.Tip_user,
		"id_user":  user.Id_user,
	})
}
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
func (ctx *CContext) GetAbonamentActiv(c *gin.Context) {
	idUserStr := c.Param("id_user")
	idUser, err := strconv.Atoi(idUserStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID invalid"})
		return
	}

	var abonament Abonament
	err = ctx.DB.
		Where("id_user = ? AND data_finalizare >= ? AND tip_abonament != ?", idUser, time.Now(), "NEACTIV").
		Order("data_finalizare DESC").
		First(&abonament).Error

	if err == nil {
		c.JSON(http.StatusOK, abonament)
		return
	}

	err = ctx.DB.
		Model(&Abonament{}).
		Where("id_user = ? AND data_finalizare < ? AND tip_abonament != ?", idUser, time.Now(), "NEACTIV").
		Updates(map[string]interface{}{
			"tip_abonament": "NEACTIV",
			"numar_sedinte": 0,
		}).Error

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la actualizarea abonamentelor"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"tip_abonament": "NEACTIV",
		"numar_sedinte": 0,
	})
}

func (ctx *CContext) AddAbonament(c *gin.Context) {
	var req struct {
		IDUser       int    `json:"id_user"`
		TipAbonament string `json:"tip_abonament"`
	}

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Date invalide"})
		return
	}

	var numarSedinte int
	switch req.TipAbonament {
	case "standard":
		numarSedinte = 12
	case "gold":
		numarSedinte = 13
	case "premium":
		numarSedinte = 15
	default:
		c.JSON(http.StatusBadRequest, gin.H{"error": "Tip abonament invalid"})
		return
	}

	if err := ctx.DB.Model(&Abonament{}).
		Where("ID_USER = ? AND TIP_ABONAMENT != ?", req.IDUser, "NEACTIV").
		Update("TIP_ABONAMENT", "NEACTIV").Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la dezactivare abonamente vechi"})
		return
	}

	abonament := Abonament{
		IDUser:         req.IDUser,
		TipAbonament:   req.TipAbonament,
		NumarSedinte:   numarSedinte,
		DataStart:      time.Now(),
		DataFinalizare: time.Now().AddDate(0, 1, 0),
	}

	if err := ctx.DB.Create(&abonament).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la creare abonament"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"message":   "Abonament adăugat",
		"abonament": abonament,
	})
}
func (ctx *CContext) DeactivateAbonament(c *gin.Context) {
	var req struct {
		IDUser int `json:"id_user"`
	}

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Date invalide"})
		return
	}

	if err := ctx.DB.Model(&Abonament{}).
		Where("ID_USER = ? AND TIP_ABONAMENT != ?", req.IDUser, "NEACTIV").
		Updates(map[string]interface{}{
			"TIP_ABONAMENT": "NEACTIV",
			"NUMAR_SEDINTE": 0,
		}).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la dezactivare"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Abonament dezactivat"})
}
func (ctx *CContext) GetIstoricAbonamente(c *gin.Context) {
	idUserStr := c.Param("id_user")
	idUser, err := strconv.Atoi(idUserStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID invalid"})
		return
	}

	var abonamente []Abonament
	err = ctx.DB.
		Where("id_user = ?", idUser).
		Order("data_finalizare DESC").
		Find(&abonamente).Error

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la interogare"})
		return
	}

	c.JSON(http.StatusOK, abonamente)
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
		Where("ID_USER = ? AND CITIT = ?", idUser, false).
		Update("CITIT", true).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la actualizare notificări"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Toate notificările au fost marcate ca citite"})
}
