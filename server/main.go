package main

import (
	"fmt"
	"log"
	"os"
)

func main() {
	fmt.Println("Serverul a pornit")

	context, err := ConfInit()
	if err != nil {
		log.Fatalf("DB: %s", err)
	}
	RegisterObserver(NotificationSaver{DB: context.DB})
	port := os.Getenv("API_PORT")
	r := ConfigRouter(context)
	err = r.Run(fmt.Sprintf(":%s", port))
	if err != nil {
		log.Fatalf("error %s", err)
	}
}
