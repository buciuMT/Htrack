package main

import (
	"github.com/gin-gonic/gin"
)

func (self *CContext) Login(c *gin.Context) {
	email, c1 := c.Params.Get("username")
	password, c2 := c.Params.Get("password")
	if !(c1 && c2) {
		c.JSON(200, gin.H{
			"message": "NOT FOUND",
		})
		return
	}
	_ = email
	_ = password
	c.JSON(200, gin.H{
		"message": "pong",
	})
}
