package main

import (
	"github.com/gin-gonic/gin"
)

func ConfigRouter(context *CContext) *gin.Engine {
	router := gin.Default()

	router.GET("/ping", context.Ping)
	router.POST("/login", context.C_Login)
	router.POST("/register", context.C_Register)
<<<<<<< HEAD

	auth := router.Group("/priv/")
	auth.Use(context.MID_auth)
	auth.GET("/me", func(c *gin.Context) {
		user := c.MustGet("user").(User)

		c.JSON(200, gin.H{
			"user_id": user.Id_user,
		})
	})
=======
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

>>>>>>> ceea3e1f9164b2f7126f0ff54d405ba4175351c3
	return router
}
