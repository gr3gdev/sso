const loginRouter = require('express').Router()
const jwtUtil = require('../utils/jwt-util')
const common = require('./common')
const logger = require('../logger')
const db = require('../database')

const errors = {
    1: 'Login or password invalid',
    2: 'Session expired'
}

loginRouter.route('/')
    .get((req, res) => {
        // Access by client
        const authHeader = req.headers['authorization']
        const token = authHeader && authHeader.split(' ')[1]
        if (token == null) {
            res.sendStatus(401)
        } else {
            jwtUtil.checkClient(token, (err, client) => {
                if (err) {
                    res.sendStatus(401)
                } else {
                    logger.info(`Access for client : ${client.name}`)
                    res.render('login', {
                        error: req.query.error ? errors[req.query.error] : null,
                        redirect: req.query.redirect
                    })
                }
            })
        }
    })
    .post((req, res) => {
        const redirect = req.query.redirect
        const username = req.body.username
        const password = req.body.password
        logger.info(`Login tentative for ${username}`)
        db.findUser(username, password, (user, err) => {
            if (err) {
                res.redirect(`/login?redirect=${redirect}&error=1`)
            } else {
                // Cookie with jwt token
                common.redirectToUrl(user, res, redirect)
            }
        })
    })

module.exports = loginRouter
