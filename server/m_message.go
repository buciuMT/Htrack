package main

import "time"

type Message struct {
	IDMessage      int       `gorm:"column:ID_MESSAGE;primaryKey" json:"id_message"`
	IDConversation int       `gorm:"column:ID_CONVERSATION" json:"id_conversation"`
	IDSender       int       `gorm:"column:ID_SENDER" json:"id_sender"`
	Mesaj          string    `gorm:"column:MESAJ" json:"mesaj"`
	Timestamp      time.Time `gorm:"column:TIMESTAMP" json:"timestamp"`
	Vazut          bool      `gorm:"column:VAZUT" json:"vazut"`

	Sender       User         `gorm:"foreignKey:IDSender" json:"-"`
	Conversation Conversation `gorm:"foreignKey:IDConversation" json:"-"`
}
