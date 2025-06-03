package main

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

func (Aliment) TableName() string {
	return "aliment"
}
