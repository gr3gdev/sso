const bcrypt = require('bcryptjs')
const logger = require('../logger')
const common = require('./common')

module.exports = {
    add: (pool, user, next) => {
        bcrypt.genSalt(10, (err, salt) => {
            bcrypt.hash(user.password, salt, (err, hash) => {
                if (err) {
                    logger.error(err.stack)
                } else {
                    const query = {
                        name: 'add-user',
                        text: 'INSERT INTO public.user (username, password) VALUES ($1, $2)',
                        values: [user.username, hash]
                    }
                    common.save(pool, query, next)
                }
            })
        })
    },
    update: (pool, user, next) => {
        if (user.password) {
            bcrypt.genSalt(10, (err, salt) => {
                bcrypt.hash(user.password, salt, (err, hash) => {
                    if (err) {
                        logger.error(err.stack)
                    } else {
                        const query = {
                            name: 'update-user',
                            text: 'UPDATE public.user SET password=$2 WHERE username=$1)',
                            values: [user.username, hash]
                        }
                        common.save(pool, query, next)
                    }
                })
            })
        }
        // TODO update role
    },
    findAll: (pool, next) => {
        const query = {
            name: 'find-users',
            text: 'SELECT * FROM public.user'
        }
        common.select(pool, query, next)
    },
    find: (pool, username, password, next) => {
        const query = {
            name: 'find-user',
            text: 'SELECT * FROM public.user WHERE username = $1',
            values: [username]
        }
        common.selectOneLine(pool, query, (user) => {
            if (user) {
                bcrypt.compare(password, user.password, (err, result) => {
                    if (err) {
                        logger.error(err.stack)
                        next(null, 'User not found')
                    } else {
                        delete user.password
                        logger.info('User found')
                        next(user, null)
                    }
                })
            } else {
                next(null, 'User not found')
            }
        })
    }
}
