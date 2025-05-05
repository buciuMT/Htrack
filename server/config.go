package main

import (
	"fmt"
	"log"
	"os"

	"gorm.io/driver/mysql"
	"gorm.io/gorm"
)

// func dbInit() error {
// 	user := os.Getenv("MYSQL_USER")
// 	password := os.Getenv("MYSQL_PASSWORD")
// 	host := os.Getenv("DB_HOST")
// 	dbname := os.Getenv("DB_NAME")
// 	dsn := fmt.Sprintf("%s:%s@tcp(%s)/%s?charset=utf8mb4&parseTime=True&loc=Local", user, password, host, dbname)
// 	var err error
// 	DB, err = sql.Open("mysql", dsn)
// 	if err != nil {
// 		return err
// 	}
// 	return DB.Ping()
// }

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

func ConfInit() (*CContext, error) {
	db, err := gormInit()
	context := &CContext{db}
	if err != nil {
		return nil, err
	}
	log.Println("Connected to the DB")
	return context, nil
}
