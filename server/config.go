package main

import (
	"fmt"
	"log"
	"os"
	"sync"

	"gorm.io/driver/mysql"
	"gorm.io/gorm"
)


var (
	instance *CContext
	once     sync.Once
)

// gormInit rămâne neschimbată
func gormInit() (*gorm.DB, error) {
	user := os.Getenv("MYSQL_USER")
	password := os.Getenv("MYSQL_PASSWORD")
	host := os.Getenv("DB_HOST")
	dbname := os.Getenv("DB_NAME")

	dsn := fmt.Sprintf("%s:%s@tcp(%s)/%s?charset=utf8mb4&parseTime=True&loc=Local", user, password, host, dbname)
	db, err := gorm.Open(mysql.Open(dsn), &gorm.Config{})
	if err != nil {
		return nil, err
	}

	return db, nil
}

// ConfInit Singleton
func ConfInit() (*CContext, error) {
	var err error

	once.Do(func() {
		var db *gorm.DB
		db, err = gormInit()
		if err != nil {
			return
		}
		instance = &CContext{DB: db}
		log.Println("Connected to the DB")
	})

	if instance == nil {
		return nil, err
	}

	return instance, nil
}
