package router

import (
	"sv/controllers"

	"github.com/gin-gonic/gin"
)

func ConfigRouter() *gin.Engine {
	router := gin.Default()
	router.GET("/ping", controllers.Ping)
	return router
}
