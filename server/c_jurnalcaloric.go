package main

import (
	"fmt"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
)

// Request struct for searching aliments
type SearchAlimentRequest struct {
	Query string `json:"query"`
}

// Request struct for adding aliment to journal
type AddAlimentToJournalRequest struct {
	IDUser    int    `json:"id_user"`
	IDAliment int    `json:"id_aliment"`
	TipMasa   string `json:"tip_masa"`
	Cantitate int    `json:"cantitate"`
	Data      string `json:"data"` // YYYY-MM-DD
}

// Request struct for removing aliment from journal
type RemoveAlimentFromJournalRequest struct {
	IDJurnalAlimentar int `json:"id_jurnal_alimentar"`
}

// Handler to search for aliments
func (ctx *CContext) SearchAlimente(c *gin.Context) {
	query := c.Query("query") // Get query from URL parameter
	if query == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Query parameter is required"})
		return
	}

	var aliments []Aliment
	err := ctx.DB.Where("nume LIKE ?", "%"+query+"%").Find(&aliments).Error
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la căutarea alimentelor: " + err.Error()})
		return
	}

	if len(aliments) == 0 {
		c.JSON(http.StatusNotFound, gin.H{"message": "Nu s-au găsit alimente."})
		return
	}

	c.JSON(http.StatusOK, aliments)
}

// Handler to add an aliment to a user's journal
func (ctx *CContext) AddAlimentToJournal(c *gin.Context) {
	var req AddAlimentToJournalRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		fmt.Println("Error binding JSON for AddAlimentToJournal:", err.Error()) // <--- THIS IS KEY
		c.JSON(http.StatusBadRequest, gin.H{"error": "Date invalide: " + err.Error()})
		return
	}
	fmt.Printf("Received AddAlimentToJournal request: %+v\n", req) // <--- THIS IS KEY

	parsedDate, err := time.Parse("2006-01-02", req.Data)
	if err != nil {
		fmt.Println("Error parsing date:", err.Error()) // <--- THIS IS KEY
		c.JSON(http.StatusBadRequest, gin.H{"error": "Format dată invalid. Folosiți YYYY-MM-DD."})
		return
	}

	jurnalEntry := JurnalAlimentar{
		IDUser:       req.IDUser,
		IDAliment:    req.IDAliment,
		TipMasa:      req.TipMasa,
		Cantitate:    req.Cantitate,
		DataAdaugare: parsedDate,
	}

	if err := ctx.DB.Create(&jurnalEntry).Error; err != nil {
		fmt.Println("Error creating JurnalAlimentar entry:", err.Error()) // <--- THIS IS KEY
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la adăugarea alimentului în jurnal: " + err.Error()})
		return
	}

	fmt.Println("Aliment added to journal successfully.") // <--- THIS IS KEY
	c.JSON(http.StatusOK, gin.H{
		"message": "Aliment adăugat în jurnal",
		"entry":   jurnalEntry,
	})
}

// Handler to remove an aliment from a user's journal
func (ctx *CContext) RemoveAlimentFromJournal(c *gin.Context) {
	var req RemoveAlimentFromJournalRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Date invalide"})
		return
	}

	result := ctx.DB.Delete(&JurnalAlimentar{}, req.IDJurnalAlimentar)
	if result.Error != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la ștergerea alimentului din jurnal: " + result.Error.Error()})
		return
	}

	if result.RowsAffected == 0 {
		c.JSON(http.StatusNotFound, gin.H{"error": "Intrarea în jurnal nu a fost găsită."})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Aliment șters din jurnal"})
}

// Handler to get food journal entries for a specific user, date, and meal type
func (ctx *CContext) GetJournalEntriesByDateAndMeal(c *gin.Context) {
	idUserStr := c.Param("id_user")
	dateStr := c.Param("date") // YYYY-MM-DD
	tipMasa := c.Param("tip_masa")

	idUser, err := strconv.Atoi(idUserStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID user invalid"})
		return
	}

	parsedDate, err := time.Parse("2006-01-02", dateStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Format dată invalid. Folosiți YYYY-MM-DD."})
		return
	}

	var entries []JurnalAlimentar
	err = ctx.DB.
		Preload("Aliment"). // This will load the related Aliment data
		Where("id_user = ? AND tip_masa = ? AND DATE(data_adaugare) = DATE(?)", idUser, tipMasa, parsedDate).
		Find(&entries).Error
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la obținerea intrărilor în jurnal: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, entries)
}

func (ctx *CContext) GetDailyCalories(c *gin.Context) {
	idUserStr := c.Param("id_user")
	dateStr := c.Param("date") // AAAA-LL-ZZ

	idUser, err := strconv.Atoi(idUserStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID user invalid"})
		return
	}

	parsedDate, err := time.Parse("2006-01-02", dateStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Format dată invalid. Folosiți AAAA-LL-ZZ."})
		return
	}

	var totalCalories float64

	err = ctx.DB.
		Table("jurnal_alimentar").
		Select("SUM(aliment.calorii * jurnal_alimentar.cantitate / 100.0)").
		Joins("JOIN aliment ON aliment.id_aliment = jurnal_alimentar.id_aliment").
		Where("jurnal_alimentar.id_user = ? AND DATE(jurnal_alimentar.data_adaugare) = DATE(?)", idUser, parsedDate).
		Scan(&totalCalories).Error
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la calcularea caloriilor zilnice: " + err.Error()})
		return
	}

	formattedCalories := fmt.Sprintf("%.2f", totalCalories)

	c.JSON(http.StatusOK, gin.H{"total_calorii": formattedCalories})
}
