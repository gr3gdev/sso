const jwt = require('jsonwebtoken')
const crypto = require('crypto')

const userSecret = crypto.randomBytes(256).toString('base64')
const clientSecret = crypto.randomBytes(128).toString('base64')

module.exports = {
    generateUserToken: (data, callback) => {
        jwt.sign(data, userSecret, (err, token) => callback(err, token))
    },
    getClientToken: () => {
        return clientSecret
    },
    checkUser: (token, callback) => {
        jwt.verify(token, userSecret, (err, data) => callback(err, data))
    },
    checkClient: (token, callback) => {
        jwt.verify(token, clientSecret, (err, data) => callback(err, data))
    }
}