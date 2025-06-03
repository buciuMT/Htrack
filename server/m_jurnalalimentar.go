package main

import "time"

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
