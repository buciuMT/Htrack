package main

import (
	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
)

func loginUser(db *gorm.DB, email string, password string) (string, error) {
	var user User
	res := db.Where("email=?", email).First(&user)
	if res.Error != nil {
		return "", res.Error
	}
	err := bcrypt.CompareHashAndPassword([]byte(user.parola), []byte(password))
	if err != nil {
		return "", err
	}
	return GenerateNewSession(db, user.id_user), nil
}

func (ctx *CContext) C_Register(c *gin.Context) {
}

func (ctx *CContext) C_Login(c *gin.Context) {
	email, c1 := c.Params.Get("username")
	password, c2 := c.Params.Get("password")
	if !c1 || !c2 {
		c.JSON(200, gin.H{
			"success": "false",
			"message": "NOT FOUND",
		})
	}
	session, err := loginUser(ctx.DB, email, password)
	if err != nil {
		c.JSON(200, gin.H{
			"success": "false",
			"message": "NOT FOUND",
		})
		return
	}
	c.JSON(200, gin.H{
		"success": "true",
		"message": session,
	})
}
