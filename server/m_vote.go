package main

import "time"

type Vote struct {
	IDVote  uint      `gorm:"primaryKey;column:ID_VOTE" json:"id_vote"`
	IDPoll  uint      `gorm:"column:ID_POLL" json:"id_poll"`
	IDUser  uint      `gorm:"column:ID_USER" json:"id_user"`
	Ora     int       `gorm:"column:ORA" json:"ora"`
	DataVot time.Time `gorm:"column:DATA_VOT" json:"data_vot"`
}
