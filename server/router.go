package main

import (
	"github.com/gin-gonic/gin"
)

func (*CContext) Ping(c *gin.Context) {
	c.JSON(200, gin.H{
		"message": "pong",
	})
}

func ConfigRouter(context *CContext) *gin.Engine {
	router := gin.Default()
	router.GET("/ping", context.Ping)
	router.POST("/login", context.C_Login)
	router.POST("/register", context.C_Register)
	return router
}
