package main

import (
	"fmt"
	"log"
	"os"
	"sv/config"
	"sv/router"
)

func main() {
	fmt.Println("salut mihai")
	err := config.Init()
	if err != nil {
		log.Fatalf("DB: %s", err)
	}
	port := os.Getenv("API_PORT")
	r := router.ConfigRouter()
	err = r.Run(fmt.Sprintf(":%s", port))
	if err != nil {
		log.Fatalf("error %s", err)
	}
}
