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
type Poll struct {
	ID_POLL   uint      `gorm:"primaryKey;column:ID_POLL" json:"id_poll"`
	TrainerID int       `gorm:"column:ID_TRAINER" json:"trainer_id"`
	IsActive  bool      `gorm:"column:ACTIV" json:"is_active"`
	Votes     []Vote    `gorm:"foreignKey:IDPoll" json:"votes"`
	Data      time.Time `gorm:"column:DATA" json:"data"`
}

type Vote struct {
	IDVote  uint      `gorm:"primaryKey;column:ID_VOTE" json:"id_vote"`
	IDPoll  uint      `gorm:"column:ID_POLL" json:"id_poll"`
	IDUser  uint      `gorm:"column:ID_USER" json:"id_user"`
	Ora     int       `gorm:"column:ORA" json:"ora"`
	DataVot time.Time `gorm:"column:DATA_VOT" json:"data_vot"`
}

type PollOra struct {
	ID_POLL   uint      `gorm:"column:ID_POLL" json:"id_poll"`
	TrainerID int       `gorm:"column:ID_TRAINER" json:"trainer_id"`
	IsActive  bool      `gorm:"column:ACTIV" json:"is_active"`
	Data      time.Time `gorm:"column:DATA" json:"data"`
	Ora       int       `gorm:"column:ORA" json:"ora_selectata"`
}

type UpdateVoteRequest struct {
	IdPoll int `json:"id_poll"`
	IdUser int `json:"id_user"`
	Ora    int `json:"ora"`
}

type Conversation struct {
	IDConversation int       `gorm:"column:ID_CONVERSATION;primaryKey" json:"id_conversation"`
	IDUser         int       `gorm:"column:ID_USER" json:"id_user"`
	IDTrainer      int       `gorm:"column:ID_TRAINER" json:"id_trainer"`
	DataStart      time.Time `gorm:"column:DATA_START" json:"data_start"`

	User    User `gorm:"foreignKey:IDUser" json:"-"`
	Trainer User `gorm:"foreignKey:IDTrainer" json:"-"`
}

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

type SendMessageRequest struct {
	IDConversation int    `json:"id_conversation"`
	IDSender       int    `json:"id_sender"`
	Mesaj          string `json:"mesaj"`
}

type MessageResponse struct {
	IDMessage int       `json:"id_message"`
	IDSender  int       `json:"id_sender"`
	Username  string    `json:"username"`
	Mesaj     string    `json:"mesaj"`
	Timestamp time.Time `json:"timestamp"`
	Vazut     bool      `json:"vazut"`
}

type Aliment struct {
	ID_Aliment         int     `gorm:"primaryKey;column:id_aliment"`
	Nume               string  `gorm:"column:nume"`
	Calorii            float64 `gorm:"column:calorii"`
	Proteine           float64 `gorm:"column:proteine"`
	Carbohidrati       float64 `gorm:"column:carbohidrati"`
	Zaharuri           float64 `gorm:"column:zaharuri"`
	Grasimi_Saturate   float64 `gorm:"column:grasimi_saturate"`
	Grasimi_Nesaturate float64 `gorm:"column:grasimi_nesaturate"`
	Fibre              float64 `gorm:"column:fibre"`
}
type JurnalAlimentar struct {
	IDJurnalAlimentar int       `gorm:"column:ID_JURNAL_ALIMENTAR;primaryKey" json:"id_jurnal_alimentar"`
	IDUser            int       `gorm:"column:ID_USER" json:"id_user"`
	IDAliment         int       `gorm:"column:ID_ALIMENT" json:"id_aliment"`
	TipMasa           string    `gorm:"column:TIP_MASA" json:"tip_masa"`
	Cantitate         int       `gorm:"column:CANTITATE" json:"cantitate"`
	DataAdaugare      time.Time `gorm:"column:DATA_ADAUGARE" json:"data_adaugare"`

	Aliment Aliment `gorm:"foreignKey:IDAliment" json:"aliment"`
}

func (JurnalAlimentar) TableName() string {
	return "jurnal_alimentar"
}
func (Aliment) TableName() string {
	return "aliment"
}
func (User) TableName() string {
	return "users"
}

func (Notificare) TableName() string {
	return "notificari"
}
