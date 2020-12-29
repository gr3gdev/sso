const logger = require('../logger')
const common = require('./common')

module.exports = {
    add: (pool, clientId, ips, next) => {
        let sql = 'INSERT INTO public.client_addresses (client_id, ip) VALUES '
        ips.forEach(ip => {
            sql += `(${clientId}, '${ip}'),`
        })
        const query = {
            name: 'add-client-addresses',
            text: sql.substring(0, sql.length - 1)
        }
        common.save(pool, query, next)
    },
    findByClient: (pool, clientID, next) => {
        const query = {
            name: 'find-addresses',
            text: 'SELECT ip FROM public.client_addresses WHERE client_id = $1',
            values: [clientID]
        }
        common.select(pool, query, next)
    },
    find: (pool, ip, name, next) => {
        const query = {
            name: 'find-address',
            text: 'SELECT c.id FROM public.client_addresses ca INNER JOIN public.client c ON c.id = ca.client_id WHERE ca.ip = $1 AND c.name = $2',
            values: [ip, name]
        }
        common.selectOneLine(pool, query, (client) => {
            next(client)
        })
    }
}
