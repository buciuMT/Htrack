package main

import "time"

type Abonament struct {
	IDAbonament    int       `gorm:"column:ID_ABONAMENT;primaryKey" json:"ID_ABONAMENT"`
	IDUser         int       `gorm:"column:ID_USER" json:"ID_USER"`
	TipAbonament   string    `gorm:"column:TIP_ABONAMENT" json:"TIP_ABONAMENT"`
	NumarSedinte   int       `gorm:"column:NUMAR_SEDINTE" json:"NUMAR_SEDINTE"`
	DataStart      time.Time `gorm:"column:DATA_START" json:"DATA_START"`
	DataFinalizare time.Time `gorm:"column:DATA_FINALIZARE" json:"DATA_FINALIZARE"`
}
