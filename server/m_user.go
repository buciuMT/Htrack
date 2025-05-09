package main

type User struct {
	Id_user  int64 `gorm:"primaryKey;autoIncrement"`
	Username string
	Email    string
	Parola   string
	Tip_user string
}
