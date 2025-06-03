package main

import (
	"fmt"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
)

type SendMessageRequest struct {
	IDConversation int    `json:"id_conversation"`
	IDSender       int    `json:"id_sender"`
	Mesaj          string `json:"mesaj"`
}

type MessageResponse struct {
	IDMessage int       `json:"id_message"`
	IDSender  int       `json:"id_sender"`
	Username  string    `json:"username"`
	Mesaj     string    `json:"mesaj"`
	Timestamp time.Time `json:"timestamp"`
	Vazut     bool      `json:"vazut"`
}

type StartChatRequest struct {
	IDUser    uint `json:"id_user"`
	IDTrainer uint `json:"id_trainer"`
}

func (ctx *CContext) StartChat(c *gin.Context) {
	var req struct {
		IdUser    int `json:"id_user"`
		IdTrainer int `json:"id_trainer"`
	}

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid input"})
		return
	}

	if req.IdUser == 0 || req.IdTrainer == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Missing user or trainer ID"})
		return
	}

	// Verifică dacă deja există conversația
	var convId int
	err := ctx.DB.
		Table("conversations").
		Select("ID_CONVERSATION").
		Where("ID_USER = ? AND ID_TRAINER = ?", req.IdUser, req.IdTrainer).
		Scan(&convId).Error

	if err == nil && convId != 0 {
		// Conversație deja existentă
		c.JSON(http.StatusOK, gin.H{"id_conversatie": convId})
		return
	}

	// Altfel, creează una nouă
	result := ctx.DB.Exec(
		"INSERT INTO conversations (ID_USER, ID_TRAINER) VALUES (?, ?)",
		req.IdUser, req.IdTrainer,
	)
	if result.Error != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": result.Error.Error()})
		return
	}

	var newID int
	ctx.DB.Raw("SELECT LAST_INSERT_ID()").Scan(&newID)

	c.JSON(http.StatusOK, gin.H{"id_conversatie": newID})
}

func (ctx *CContext) SendMessage(c *gin.Context) {
	var req SendMessageRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Date invalide"})
		return
	}

	mesaj := Message{
		IDConversation: req.IDConversation,
		IDSender:       req.IDSender,
		Mesaj:          req.Mesaj,
		Timestamp:      time.Now(),
		Vazut:          false,
	}

	if err := ctx.DB.Create(&mesaj).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la trimiterea mesajului"})
		return
	}

	// Observer Pattern: notificare observatori (ex: trigger pentru WebSocket, log, etc.)
	NotifyObservers(mesaj)

	c.JSON(http.StatusOK, gin.H{
		"message": "Mesaj trimis",
		"id":      mesaj.IDMessage,
	})
}

type MessageObserver interface {
	OnMessageSent(msg Message)
}

var observers []MessageObserver

func RegisterObserver(o MessageObserver) {
	observers = append(observers, o)
}

func NotifyObservers(msg Message) {
	for _, o := range observers {
		fmt.Println("NotifyObservers called")
		o.OnMessageSent(msg)
	}
}

func (ctx *CContext) MarcareMesajeVazute(c *gin.Context) {
	idConvStr := c.Param("id_conversation")
	idUserStr := c.Param("id_user")

	idConv, err := strconv.Atoi(idConvStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID conversație invalid"})
		return
	}
	idUser, err := strconv.Atoi(idUserStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID user invalid"})
		return
	}

	result := ctx.DB.Model(&Message{}).
		Where("id_conversation = ? AND id_sender != ? AND vazut = FALSE", idConv, idUser).
		Update("vazut", true)

	if result.Error != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la marcarea mesajelor"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Mesajele au fost marcate ca văzute"})
}

func (ctx *CContext) GetMessagesForConversation(c *gin.Context) {
	conversationIDStr := c.Param("id_conversation")
	conversationID, err := strconv.Atoi(conversationIDStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID conversație invalid"})
		return
	}

	var messages []MessageResponse
	err = ctx.DB.Table("messages").
		Select("messages.id_message, messages.id_sender, users.username, messages.mesaj, messages.timestamp, messages.vazut").
		Joins("JOIN users ON users.id_user = messages.id_sender").
		Where("messages.id_conversation = ?", conversationID).
		Order("messages.timestamp ASC").
		Scan(&messages).Error
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la obținerea mesajelor"})
		return
	}

	c.JSON(http.StatusOK, messages)
}

type MarkAsSeenRequest struct {
	IDConversation int `json:"id_conversation"`
	IDUser         int `json:"id_user"`
}

func (ctx *CContext) MarkMessagesAsSeen(c *gin.Context) {
	var req MarkAsSeenRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Date invalide"})
		return
	}

	err := ctx.DB.Model(&Message{}).
		Where("id_conversation = ? AND id_sender != ? AND vazut = ?", req.IDConversation, req.IDUser, false).
		Update("vazut", true).Error
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la actualizarea mesajelor"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Mesajele au fost marcate ca văzute"})
}

func (ctx *CContext) GetConversationsForUser(c *gin.Context) {
	idUserStr := c.Param("id_user")
	idUser, err := strconv.Atoi(idUserStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID user invalid"})
		return
	}

	var conversatii []Conversation
	if err := ctx.DB.
		Where("id_user = ?", idUser).
		Find(&conversatii).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Eroare la obținerea conversațiilor"})
		return
	}

	c.JSON(http.StatusOK, conversatii)
}
