package main

import "gorm.io/gorm"

type CContext struct {
	DB *gorm.DB
}

func NewCContext(db *gorm.DB) *CContext {
	return &CContext{DB: db}
}
