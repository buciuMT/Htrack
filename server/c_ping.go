package main

import "github.com/gin-gonic/gin"

func (*CContext) Ping(c *gin.Context) {
	c.JSON(200, gin.H{
		"message": "pong",
	})
}
