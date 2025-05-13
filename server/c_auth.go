package main

import (
	"fmt"

	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
)

type LoginRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

func loginUser(db *gorm.DB, username string, password string) (string, error) {
	var user User
	res := db.Where("username=?", username).First(&user)
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
	var req LoginRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{
			"success": false,
			"message": "INVALID JSON",
		})
		return
	}

	fmt.Println("LOGIN JSON:", req.Username, req.Password)

	var user User
	res := ctx.DB.Debug().Where("username = ?", req.Username).First(&user)

	fmt.Println("Error:", res.Error)
	fmt.Printf("User: %+v\n", user)

	if res.Statement != nil {
		fmt.Println("SQL:", res.Statement.SQL.String())
	} else {
		fmt.Println("❌ SQL statement nu a fost generat.")
	}

	if res.Error != nil {
		fmt.Println("User not found:", res.Error)
		c.JSON(404, gin.H{
			"success": false,
			"message": "User not found",
		})
		return
	}

	// ➡️ Aici vezi ce ai în baza de date
	fmt.Println("DEBUG: Am găsit user-ul:")
	fmt.Println("Username:", user.Username)
	fmt.Println("Parola:", user.Parola)
	fmt.Println("Tip User:", user.Tip_user)

	// ➡️ Comparația parolelor (DOAR PENTRU TEST)
	if user.Parola != req.Password {
		fmt.Println("Wrong password")
		fmt.Println("Parola introdusă:", req.Password)
		fmt.Println("Parola din DB:", user.Parola)

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
