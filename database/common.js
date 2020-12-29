const logger = require('../logger')

module.exports = {
    selectOneLine: (pool, query, next) => {
        pool.connect((err, client, done) => {
            if (err) {
                throw err
            }
            client.query(query, (err, res) => {
                done()
                if (err) {
                    logger.error(err.stack)
                } else {
                    if (res.rows.length > 1) {
                        logger.error(`More than one line`)
                    } else if (res.rows.length === 1) {
                        next(res.rows[0])
                    } else {
                        next(null)
                    }
                }
            })
        })
    },
    select: (pool, query, next) => {
        pool.connect((err, client, done) => {
            if (err) {
                throw err
            }
            client.query(query, (err, res) => {
                done()
                if (err) {
                    logger.error(err.stack)
                } else {
                    next(res.rows)
                }
            })
        })
    },
    save: (pool, query, next) => {
        pool.connect((err, client, done) => {
            if (err) {
                throw err
            }
            client.query(query, (err, res) => {
                done()
                if (err) {
                    logger.error(err.stack)
                } else {
                    if (res.rows[0]) {
                        next(res.rows[0].id)
                    } else {
                        next()
                    }
                }
            })
        })
    }
}
