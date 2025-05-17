package main

type User struct {
	Id_user  int64  `gorm:"column:ID_USER;primaryKey" json:"id_user"`
	Email    string `gorm:"column:EMAIL" json:"email"`
	Username string `gorm:"column:USERNAME" json:"username"`
	Parola   string `gorm:"column:PAROLA" json:"parola"`
	Tip_user string `gorm:"column:TIP_USER" json:"tip_user"`
}

func (User) TableName() string {
	return "users"
}
