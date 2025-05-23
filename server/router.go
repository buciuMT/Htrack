package main

import (
	"github.com/gin-gonic/gin"
)

func ConfigRouter(context *CContext) *gin.Engine {
	router := gin.Default()

	router.GET("/ping", context.Ping)
	router.POST("/login", context.C_Login)
	router.POST("/register", context.C_Register)

	auth := router.Group("/priv/")
	auth.Use(context.MID_auth)
	auth.GET("/me", func(c *gin.Context) {
		user := c.MustGet("user").(User)

		c.JSON(200, gin.H{
			"user_id": user.Id_user,
		})
	})
	return router
}
