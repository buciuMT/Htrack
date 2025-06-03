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
	router.POST("/notificari", context.AddNotificare)
	router.GET("/notificari/:id_user", context.GetNotificariUser)
	router.POST("/notificari/citit/:id_user", context.MarcheazaNotificariCitite)
	router.POST("/poll", context.AddPoll)
	router.POST("/poll/dezactivare/:id_poll", context.DeactivatePoll)
	router.GET("/poll/trainer/:id_trainer", context.GetPollTrainer)
	router.POST("/vote", context.SubmitVote)
	router.GET("/poll/user/:id_user", context.GetPollByUser)
	router.GET("/votes/:poll_id/:user_id", context.GetVoteByUserAndPoll)
	router.GET("/poll/:id_poll/votes", context.GetVotesForPoll)
	router.GET("/polls/votate/:id_user", context.GetPollsVotateDeUser)
	router.POST("/vote/update", context.UpdateVoteHour)
	router.POST("/chat/start", context.StartChat)
	router.POST("/messages", context.SendMessage)
	router.POST("/conversations/:id_conversation/users/:id_user/mark-seen", context.MarcareMesajeVazute)
	router.GET("/messages/:id_conversation", context.GetMessagesForConversation)
	router.POST("/messages/seen", context.MarkMessagesAsSeen)
	router.GET("/conversations/user/:id_user", context.GetConversationsForUser)
	router.GET("/users/:id_user/trainer", context.GetTrainerForUser)
	router.GET("/trainer/users/:trainerId", context.GetUsersForTrainer)
	router.GET("/alimente/search", context.SearchAlimente)
	router.POST("/jurnal-alimentar/add", context.AddAlimentToJournal)
	router.POST("/jurnal-alimentar/remove", context.RemoveAlimentFromJournal)
	router.GET("/jurnal-alimentar/:id_user/:date/:tip_masa", context.GetJournalEntriesByDateAndMeal)
	router.GET("/jurnal-alimentar/calorii/:id_user/:date", context.GetDailyCalories)
	_ = logged_in
	return router
}
