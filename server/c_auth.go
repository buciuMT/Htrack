package main

import (
	"fmt"
	"net/http"
	"strconv"

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
