package main

import (
	"github.com/google/uuid"
	"gorm.io/gorm"
)

type Session struct {
	id_session string `gorm:"primaryKey"`
	id_user    int64  `gorm:"foreignKey"`
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
	ses = Session{id.String(), user_id}
	db.Create(&ses)
	return ses.id_session
}
