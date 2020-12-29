const bcrypt = require('bcryptjs')
const logger = require('../logger')
const common = require('./common')
const addressesDB = require('./addresses')

module.exports = {
    add: (pool, client, next) => {
        bcrypt.genSalt(8, (err, salt) => {
            bcrypt.hash(client.password, salt, (err, hash) => {
                if (err) {
                    logger.error(err.stack)
                } else {
                    const query = {
                        name: 'add-client',
                        text: 'INSERT INTO public.client (name, password) VALUES ($1, $2) RETURNING id',
                        values: [client.name, hash]
                    }
                    common.save(pool, query, (id) => {
                        if (client.ips) {
                            // Add IP addresses
                            addressesDB.add(pool, id, client.ips, next)
                        } else {
                            next()
                        }
                    })
                }
            })
        })
    },
    update: (pool, client, next) => {
        if (client.password) {
            bcrypt.genSalt(8, (err, salt) => {
                bcrypt.hash(client.password, salt, (err, hash) => {
                    if (err) {
                        logger.error(err.stack)
                    } else {
                        const query = {
                            name: 'update-client',
                            text: 'UPDATE public.client SET password=$2 WHERE name=$1)',
                            values: [client.username, hash]
                        }
                        common.save(pool, query, next)
                    }
                })
            })
        }
        // TODO update addresses
    },
    findAll: (pool, next) => {
        const query = {
            name: 'find-clients',
            text: 'SELECT c.*, ca.ip FROM public.client c INNER JOIN public.client_addresses ca ON ca.client_id = c.id'
        }
        const clients = []
        common.select(pool, query, (lines) => {
            lines.forEach(line => {
                let client = clients.filter(c => c.id === line.id)[0]
                if (!client) {
                    client = {
                        id: line.id,
                        name: line.name,
                        ips: []
                    }
                    clients.push(client)
                }
                client.ips.push(line.ip)
            })
            clients.forEach(c => c.ips = c.ips.join(','))
            next(clients)
        })
    },
    find: (pool, name, password, next) => {
        const query = {
            name: 'find-client',
            text: 'SELECT * FROM public.client WHERE name = $1',
            values: [name]
        }
        common.selectOneLine(pool, query, (client) => {
            if (client) {
                bcrypt.compare(password, client.password, (err, result) => {
                    if (err) {
                        logger.error(err.stack)
                        next(null, 'Client not found')
                    } else {
                        delete client.password
                        next(client, null)
                    }
                })
            } else {
                next(null, 'Client not found')
            }
        })
    }
}
