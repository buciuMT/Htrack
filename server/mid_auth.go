package main

import (
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
)

func (ctx *CContext) MID_auth(c *gin.Context) {
	authsessions := c.GetHeader("Authorization")
	token := strings.Split(authsessions, " ")[1]
	var ses Session
	res := ctx.DB.Where("id_session = ?", token).First(&ses)
	if res.Error != nil {
		c.JSON(http.StatusNotFound, "invalid Session")
		return
	}
	var user User
	res = ctx.DB.Where("ID_USER = ?", ses.IdUser).First(&user)

	if res.Error != nil {
		c.JSON(http.StatusNotFound, "invalid User")
		return
	}

	c.Set("user", user)
	c.Next()
}
