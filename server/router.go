package main

import (
	"github.com/gin-gonic/gin"
)

func ConfigRouter(context *CContext) *gin.Engine {
	router := gin.Default()

	router.GET("/ping", context.Ping)
	router.POST("/login", context.C_Login)
	router.POST("/register", context.C_Register)
	logged_in := router.Group("/")

	logged_in.Use(context.MID_auth)
	{
		admin := logged_in.Group("/")
		admin.Use(context.MID_admin)
		{
			admin.GET("/users", context.GetUsers)
			admin.POST("/users/:id/transform-trainer", context.TransformUserToTrainer)
			admin.GET("/trainers", context.GetTrainers)
			admin.GET("/users/fara-antrenor", context.GetUsersFaraAntrenor)
			admin.POST("/users/:id/assign-trainer/:trainerId", context.SetAntrenorLaUser)
			admin.GET("/users/by-trainer/:trainerId", context.GetUsersByTrainer)
			admin.GET("/abonament/:id_user", context.GetAbonamentActiv)
			admin.POST("/abonament", context.AddAbonament)
			admin.POST("/abonament/dezactivare", context.DeactivateAbonament)
			admin.GET("/istoricAbonamente/:id_user", context.GetIstoricAbonamente)
		}
	}
	return router
}
