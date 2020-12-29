const tokenRouter = require('express').Router()
const jwtUtil = require('../utils/jwt-util')
const logger = require('../logger')
const db = require('../database')

tokenRouter.route('/')
    .get((req, res) => {
        // Check client whitelist
        const name = req.query.name
        let ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress
        if (ip.indexOf('::ffff:') > -1) {
            ip = ip.substring('::ffff:'.length)
        }
        logger.info(`Get access for ${ip}`)
        db.findAddress(ip, name, (client) => {
            if (client) {
                res.send({
                    clientId: client.id,
                    token: jwtUtil.getClientToken()
                })
            } else {
                res.sendStatus(404)
            }
        })
    })

module.exports = tokenRouter
