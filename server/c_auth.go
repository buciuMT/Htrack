package main

import (
	"fmt"

	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
)

type LoginRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
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
	var try User
	res := ctx.DB.Where("EMAIL", &try)
	if res.Error == nil {
		c.JSON(406, gin.H{
			"message": "User already exists",
		})
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
	res = ctx.DB.Create(&user)
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
		"success": true,
		"message": session,
	})
}
