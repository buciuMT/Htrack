package main

import (
	"github.com/google/uuid"
	"gorm.io/gorm"
)

type Session struct {
	IdSession string `gorm:"primaryKey;column:id_session"`
	IdUser    int64  `gorm:"column:id_user"`
}

func GenerateNewSession(db *gorm.DB, user_id int64) string {
	id := uuid.New()
	var ses Session
	for {
		res := db.First(&ses, "id_session = ?", id.String())
		if res.Error != nil {
			break
		}
		id = uuid.New()
	}

	ses = Session{IdSession: id.String(), IdUser: user_id}
	res := db.Create(&ses)
	if res.Error != nil {
		panic("Eroare la creare sesiune!")
	}
	return ses.IdSession
}
