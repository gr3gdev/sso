const indexRouter = require('express').Router()
const jwtUtil = require('../utils/jwt-util')
const common = require('./common')

indexRouter.get('/', (req, res) => {
    const redirect = req.query.redirect
    const token = req.cookies.mysso
    if (token) {
        jwtUtil.checkUser(token, (err, user) => {
            if (err) {
                res.redirect(`/login?redirect=${redirect}&error=2`)
            } else {
                common.redirectToUrl(user, res, redirect)
            }
        })
    } else {
        res.redirect(`/login?redirect=${redirect}`)
    }
})

module.exports = indexRouter