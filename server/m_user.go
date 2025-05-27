package main

import "time"

type User struct {
	Id_user     int64       `gorm:"column:ID_USER;primaryKey" json:"id_user"`
	Email       string      `gorm:"column:EMAIL" json:"email"`
	Username    string      `gorm:"column:USERNAME" json:"username"`
	Parola      string      `gorm:"column:PAROLA" json:"parola"`
	Tip_user    string      `gorm:"column:TIP_USER" json:"tip_user"`
	Antrenor_id *int64      `gorm:"column:ANTRENOR_ID" json:"antrenor_id"`
	Abonaments  []Abonament `gorm:"foreignKey:ID_USER"`
}

type Abonament struct {
	IDAbonament    int       `gorm:"column:ID_ABONAMENT;primaryKey" json:"ID_ABONAMENT"`
	IDUser         int       `gorm:"column:ID_USER" json:"ID_USER"`
	TipAbonament   string    `gorm:"column:TIP_ABONAMENT" json:"TIP_ABONAMENT"`
	NumarSedinte   int       `gorm:"column:NUMAR_SEDINTE" json:"NUMAR_SEDINTE"`
	DataStart      time.Time `gorm:"column:DATA_START" json:"DATA_START"`
	DataFinalizare time.Time `gorm:"column:DATA_FINALIZARE" json:"DATA_FINALIZARE"`
}
type Notificare struct {
	IDNotificare int       `gorm:"column:ID_NOTIFICARE;primaryKey" json:"id_notificare"`
	IDUser       int       `gorm:"column:ID_USER" json:"id_user"`
	Tip          string    `gorm:"column:TIP" json:"tip"` 
	Mesaj        string    `gorm:"column:MESAJ" json:"mesaj"`
	Data         time.Time `gorm:"column:DATA" json:"data"`
	Citit        bool      `gorm:"column:CITIT" json:"citit"`
}

func (User) TableName() string {
	return "users"
}
func (Notificare) TableName() string {
	return "notificari"
}
