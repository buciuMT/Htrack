package main

import (
	"net/http"
	"strconv"
	"time"
	"fmt"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

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
		// ✅ verificăm dacă numărul de ședințe este 0
		if abonament.NumarSedinte == 0 {
			// Actualizăm în baza de date
			ctx.DB.Model(&abonament).Updates(map[string]interface{}{
				"tip_abonament": "NEACTIV",
				"numar_sedinte": 0,
			})

			c.JSON(http.StatusOK, gin.H{
				"tip_abonament": "NEACTIV",
				"numar_sedinte": 0,
			})
			return
		}

		// Dacă are ședințe, returnăm abonamentul actual
		c.JSON(http.StatusOK, abonament)
		return
	}

	// Dacă nu găsește abonamente active, verificăm cele expirate și le dezactivăm
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

func (ctx *CContext) AddPoll(c *gin.Context) {
	var req struct {
		IDTrainer int `json:"id_trainer"`
	}

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Date invalide"})
		return
	}

	var existing Poll
	if err := ctx.DB.
		Where("trainer_id = ? AND is_active = ?", req.IDTrainer, true).
		First(&existing).Error; err == nil {
		c.JSON(http.StatusConflict, gin.H{"error": "Există deja un poll activ"})
		return
	}

	poll := Poll{
		TrainerID: req.IDTrainer,
		IsActive:  true,
		Data:      time.Now(),
	}

	if err := ctx.DB.Create(&poll).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la creare poll: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"message": "Poll creat",
		"poll":    poll,
	})
}

func (ctx *CContext) DeactivatePoll(c *gin.Context) {
	idStr := c.Param("id_poll")
	id, err := strconv.Atoi(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID invalid"})
		return
	}

	if err := ctx.DB.
		Model(&Poll{}).
		Where("ID_POLL = ?", id).
		Update("ACTIV", false).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la dezactivare poll"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Poll dezactivat"})
}
func (ctx *CContext) GetPollTrainer(c *gin.Context) {
	idTrainerStr := c.Param("id_trainer")
	idTrainer, err := strconv.Atoi(idTrainerStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID trainer invalid"})
		return
	}

	var poll Poll
	if err := ctx.DB.
		Where("ID_TRAINER = ? AND ACTIV = ?", idTrainer, true).
		First(&poll).Error; err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Niciun poll activ"})
		return
	}

	c.JSON(http.StatusOK, poll)
}
func (ctx *CContext) SubmitVote(c *gin.Context) {
	var req struct {
		IDPoll int `json:"id_poll"`
		IDUser int `json:"id_user"`
		Ora    int `json:"ora"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Date invalide"})
		return
	}

	var poll Poll
	if err := ctx.DB.First(&poll, req.IDPoll).Error; err != nil || !poll.IsActive {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Poll invalid sau inactiv"})
		return
	}

	var existing Vote
	if err := ctx.DB.
		Where("ID_POLL = ? AND ID_USER = ?", req.IDPoll, req.IDUser).
		First(&existing).Error; err == nil {
		c.JSON(http.StatusConflict, gin.H{"error": "Ai votat deja"})
		return
	}

	var abonament Abonament
	if err := ctx.DB.
		Where("ID_USER = ? AND NUMAR_SEDINTE > 0 AND TIP_ABONAMENT != ?", req.IDUser, "NEACTIV").
		First(&abonament).Error; err != nil {
		c.JSON(http.StatusForbidden, gin.H{"error": "Abonament inactiv sau sedinte epuizate"})
		return
	}

	vote := Vote{
		IDPoll:  uint(req.IDPoll),
		IDUser:  uint(req.IDUser),
		Ora:     req.Ora,
		DataVot: time.Now(),
	}
	if err := ctx.DB.Create(&vote).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la salvare vot"})
		return
	}

	ctx.DB.Model(&abonament).Update("NUMAR_SEDINTE", abonament.NumarSedinte-1)

	c.JSON(http.StatusOK, gin.H{
		"message": "Vot salvat",
		"vote":    vote,
	})
}
func (ctx *CContext) GetPollByUser(c *gin.Context) {
	idUserStr := c.Param("id_user")
	idUser, err := strconv.Atoi(idUserStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID user invalid"})
		return
	}

	var idTrainer *int
	err = ctx.DB.
		Table("users").
		Select("antrenor_id").
		Where("id_user = ?", idUser).
		Scan(&idTrainer).Error

	if err != nil || idTrainer == nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "User-ul nu are trainer asociat"})
		return
	}

	var poll Poll
	err = ctx.DB.
		Where("id_trainer = ? AND activ = ?", *idTrainer, true).
		First(&poll).Error

	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Niciun poll activ găsit pentru acest user"})
		return
	}

	c.JSON(http.StatusOK, poll)
}
func (ctx *CContext) GetVoteByUserAndPoll(c *gin.Context) {
	pollIDStr := c.Param("poll_id")
	userIDStr := c.Param("user_id")

	pollID, err := strconv.Atoi(pollIDStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID poll invalid"})
		return
	}

	userID, err := strconv.Atoi(userIDStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID user invalid"})
		return
	}

	var vote Vote
	err = ctx.DB.
		Where("id_poll = ? AND id_user = ?", pollID, userID).
		First(&vote).Error

	if err != nil {
		if err == gorm.ErrRecordNotFound {
			c.JSON(http.StatusNotFound, gin.H{"message": "Userul nu a votat în acest poll"})
		} else {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la verificarea votului"})
		}
		return
	}

	c.JSON(http.StatusOK, vote)
}
func (ctx *CContext) GetVotesForPoll(c *gin.Context) {
	pollIDStr := c.Param("id_poll")
	pollID, err := strconv.Atoi(pollIDStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID poll invalid"})
		return
	}

	var votes []struct {
		IDUser   uint      `json:"id_user"`
		Username string    `json:"username"`
		Ora      int       `json:"ora"`
		DataVot  time.Time `json:"data_vot"`
	}

	err = ctx.DB.
		Table("votes").
		Select("votes.id_user, users.username, votes.ora, votes.data_vot").
		Joins("JOIN users ON users.id_user = votes.id_user").
		Where("votes.id_poll = ?", pollID).
		Scan(&votes).Error

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la obținerea voturilor"})
		return
	}

	c.JSON(http.StatusOK, votes)
}
func (ctx *CContext) GetPollsVotateDeUser(c *gin.Context) {
	idUserStr := c.Param("id_user")
	idUser, err := strconv.Atoi(idUserStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID user invalid"})
		return
	}

	var votedPolls []PollOra

	err = ctx.DB.
		Table("polls").
		Select("polls.ID_POLL, polls.ID_TRAINER, polls.ACTIV, polls.DATA,votes.ORA").
		Joins("JOIN votes ON polls.ID_POLL = votes.ID_POLL").
		Where("votes.ID_USER = ?", idUser).
		Order("polls.DATA DESC").
		Scan(&votedPolls).Error

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la interogare: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, votedPolls)
}
func (ctx *CContext) UpdateVoteHour(c *gin.Context) {
	var req UpdateVoteRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Date invalide"})
		return
	}
	result := ctx.DB.Model(&Vote{}).
		Where("id_poll = ? AND id_user = ?", req.IdPoll, req.IdUser).
		Update("ora", req.Ora)

	if result.Error != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la actualizarea orei"})
		return
	}

	if result.RowsAffected == 0 {
		c.JSON(http.StatusNotFound, gin.H{"error": "Votul nu a fost găsit"})
		return
	}

	notificare := Notificare{
		IDUser: req.IdUser,
		Tip:    "MODIFICARE_ORA",
		Mesaj:  fmt.Sprintf("Ora ta a fost modificată la %d:00.", req.Ora),
		Data:   time.Now(),
		Citit:  false,
	}

	if err := ctx.DB.Create(&notificare).Error; err != nil {
	}

	c.JSON(http.StatusOK, gin.H{"message": "Ora actualizată și notificare trimisă"})
}
