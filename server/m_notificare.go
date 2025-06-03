package main

import "time"

type Notificare struct {
	IDNotificare int       `gorm:"column:ID_NOTIFICARE;primaryKey" json:"id_notificare"`
	IDUser       int       `gorm:"column:ID_USER" json:"id_user"`
	Tip          string    `gorm:"column:TIP" json:"tip"`
	Mesaj        string    `gorm:"column:MESAJ" json:"mesaj"`
	Data         time.Time `gorm:"column:DATA" json:"data"`
	Citit        bool      `gorm:"column:CITIT" json:"citit"`
}

func (Notificare) TableName() string {
	return "notificari"
}
