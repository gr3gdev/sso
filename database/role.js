const logger = require('../logger')
const common = require('./common')

module.exports = {
    add: (pool, role, next) => {
        const query = {
            name: 'add-role',
            text: 'INSERT INTO public.roles (client_id, user_id, role) VALUES ($1, $2, $3)',
            values: [role.client.id, role.user.id, role.name]
        }
        common.save(pool, query, next)
    },
    findAll: (pool, next) => {
        const query = {
            name: 'select-roles',
            text: 'SELECT u.username, c.name, r.role FROM public.roles r INNER JOIN public.client c ON c.id = r.client_id INNER JOIN public.user u ON u.id = r.user_id'
        }
        common.select(pool, query, next)
    }
}
