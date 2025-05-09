package main

import (
	"fmt"

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
	err := bcrypt.CompareHashAndPassword([]byte(user.Parola), []byte(password))
	if err != nil {
		return "", err
	}
	return GenerateNewSession(db, user.Id_user), nil
}

func (ctx *CContext) C_Register(c *gin.Context) {
	fmt.Println(c.Params)
	email, c1 := c.Params.Get("email")
	user_name, c2 := c.Params.Get("username")
	password, c3 := c.Params.Get("password")
	if !((!c1) || (!c2) || (!c3)) {
		c.JSON(400, gin.H{
			"message": "PARAM NOT FOUND",
		})
		return
	}
	fmt.Println("CEVA_____________________")
	fmt.Println(email)
	hashed, err := bcrypt.GenerateFromPassword([]byte(password), 0)
	if err != nil {
		c.JSON(406, gin.H{
			"message": err.Error(),
		})
		return
	}
	user := User{
		Email:    email,
		Username: user_name,
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
	email, c1 := c.Params.Get("email")
	password, c2 := c.Params.Get("password")
	if !(c1 && c2) {
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
