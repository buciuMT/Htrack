package main

type User struct {
	Id_user  int64  `gorm:"column:id_user;primaryKey"`
	Email    string `gorm:"column:email"`
	Username string `gorm:"column:username"`
	Parola   string `gorm:"column:parola"`
	Tip_user string `gorm:"column:tip_user"`
}

func (User) TableName() string {
	return "users"
}
