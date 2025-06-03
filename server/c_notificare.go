package main

import (
	"log"
	"time"

	"gorm.io/gorm"
)

func SaveNotificare(db *gorm.DB, idUser int, mesaj string, tip string) error {
	notif := Notificare{
		IDUser: idUser,
		Mesaj:  mesaj,
		Tip:    tip,
		Data:   time.Now(),
		Citit:  false,
	}
	return db.Create(&notif).Error
}

type NotificationSaver struct {
	DB *gorm.DB
}

func (n NotificationSaver) OnMessageSent(msg Message) {
	var conv struct {
		IDUser    int
		IDTrainer int
	}

	// Caută participanții la conversație
	err := n.DB.Table("conversations").
		Select("id_user, id_trainer").
		Where("id_conversation = ?", msg.IDConversation).
		Scan(&conv).Error
	if err != nil {
		log.Printf("Eroare la extragerea conversației: %v", err)
		return
	}

	var idReceiver int
	if msg.IDSender == conv.IDTrainer {
		idReceiver = conv.IDUser
	} else {
		idReceiver = conv.IDTrainer
	}

	// Salvează notificarea pentru destinatar
	err = SaveNotificare(n.DB, idReceiver, "Ai un mesaj nou", "chat")
	if err != nil {
		log.Printf("Eroare la salvarea notificării în observer: %v", err)
	}
}
