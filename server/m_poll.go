package main

import "time"

type Poll struct {
	ID_POLL   uint      `gorm:"primaryKey;column:ID_POLL" json:"id_poll"`
	TrainerID int       `gorm:"column:ID_TRAINER" json:"trainer_id"`
	IsActive  bool      `gorm:"column:ACTIV" json:"is_active"`
	Votes     []Vote    `gorm:"foreignKey:IDPoll" json:"votes"`
	Data      time.Time `gorm:"column:DATA" json:"data"`
}
