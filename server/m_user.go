package main

type User struct {
	Id_user     int64       `gorm:"column:ID_USER;primaryKey" json:"id_user"`
	Email       string      `gorm:"column:EMAIL" json:"email"`
	Username    string      `gorm:"column:USERNAME" json:"username"`
	Parola      string      `gorm:"column:PAROLA" json:"parola"`
	Tip_user    string      `gorm:"column:TIP_USER" json:"tip_user"`
	Antrenor_id *int64      `gorm:"column:ANTRENOR_ID" json:"antrenor_id"`
	Abonaments  []Abonament `gorm:"foreignKey:ID_USER"`
}

func (User) TableName() string {
	return "users"
}
