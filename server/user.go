package main

import (
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
)

type User struct {
	id_user  int64 `gorm:"primaryKey"`
	nume     string
	username string
	email    string
	parola   string
	tip_cont string
}

func LoginUser(db *gorm.DB, email string, password string) (error, string) {
	var user User
	db.Where("email=?", email).First(&user)

	err := bcrypt.CompareHashAndPassword([]byte(user.parola), []byte(password))

	return err, "nil"
}
