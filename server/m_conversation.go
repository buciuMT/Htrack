package main

import "time"

type Conversation struct {
	IDConversation int       `gorm:"column:ID_CONVERSATION;primaryKey" json:"id_conversation"`
	IDUser         int       `gorm:"column:ID_USER" json:"id_user"`
	IDTrainer      int       `gorm:"column:ID_TRAINER" json:"id_trainer"`
	DataStart      time.Time `gorm:"column:DATA_START" json:"data_start"`

	User    User `gorm:"foreignKey:IDUser" json:"-"`
	Trainer User `gorm:"foreignKey:IDTrainer" json:"-"`
}
