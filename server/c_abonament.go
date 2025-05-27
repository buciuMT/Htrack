package main

import (
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
)

func (ctx *CContext) GetAbonamentActiv(c *gin.Context) {
	idUserStr := c.Param("id_user")
	idUser, err := strconv.Atoi(idUserStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID invalid"})
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

	var abonament Abonament
	err = ctx.DB.
		Where("id_user = ? AND data_finalizare >= ?", idUser, time.Now()).
		Order("data_finalizare DESC").
		First(&abonament).Error
	if err != nil {
		c.JSON(http.StatusOK, gin.H{
			"tip_abonament": "NEACTIV",
			"numar_sedinte": 0,
		})
		return
	}

	c.JSON(http.StatusOK, abonament)
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
