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
	router.GET("/users", context.GetUsers)
	router.POST("/users/:id/transform-trainer", context.TransformUserToTrainer)
	router.GET("/trainers", context.GetTrainers)
	router.GET("/users/fara-antrenor", context.GetUsersFaraAntrenor)
	router.POST("/users/:id/assign-trainer/:trainerId", context.SetAntrenorLaUser)
	router.GET("/users/by-trainer/:trainerId", context.GetUsersByTrainer)
	router.GET("/abonament/:id_user", context.GetAbonamentActiv)
	router.POST("/abonament", context.AddAbonament)
	router.POST("/abonament/dezactivare", context.DeactivateAbonament)
	router.GET("/istoricAbonamente/:id_user", context.GetIstoricAbonamente)

	return router
}
