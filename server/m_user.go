package main

type User struct {
	Id_user  int64  `gorm:"column:ID_USER;primaryKey"`
	Email    string `gorm:"column:EMAIL"`
	Username string `gorm:"column:USERNAME"`
	Parola   string `gorm:"column:PAROLA"`
	Tip_user string `gorm:"column:TIP_USER"`
}

func (User) TableName() string {
	return "users"
}
