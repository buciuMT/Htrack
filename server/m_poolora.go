package main

import "time"

type PollOra struct {
	ID_POLL   uint      `gorm:"column:ID_POLL" json:"id_poll"`
	TrainerID int       `gorm:"column:ID_TRAINER" json:"trainer_id"`
	IsActive  bool      `gorm:"column:ACTIV" json:"is_active"`
	Data      time.Time `gorm:"column:DATA" json:"data"`
	Ora       int       `gorm:"column:ORA" json:"ora_selectata"`
}
