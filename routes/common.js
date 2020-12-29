const jwtUtil = require('../utils/jwt-util')

const cookieMaxAge = process.env.COOKIE_MAX_AGE || 24 * 60 * 60

module.exports = {
    redirectToUrl: (user, res, url) => {
        jwtUtil.generateUserToken(user, (err, token) => {
            if (err) {
                logger.error(err.stack)
                res.sendStatus(500)
            } else {
                res.cookie('mysso', token, {
                    maxAge: cookieMaxAge,
                    secure: false,
                    httpOnly: true
                })
                res.redirect(url)
            }
        })
    }
}
