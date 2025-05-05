package main

import (
	"fmt"
	"log"
	"os"
)

func main() {
	fmt.Println("salut mihai")
	context, err := ConfInit()
	if err != nil {
		log.Fatalf("DB: %s", err)
	}
	port := os.Getenv("API_PORT")
	r := ConfigRouter(context)
	err = r.Run(fmt.Sprintf(":%s", port))
	if err != nil {
		log.Fatalf("error %s", err)
	}
}
