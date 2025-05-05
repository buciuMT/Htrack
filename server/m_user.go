package main

type User struct {
	id_user  int64 `gorm:"primaryKey"`
	nume     string
	username string
	email    string
	parola   string
	tip_cont string
}
