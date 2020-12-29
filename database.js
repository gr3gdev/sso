const bcrypt = require('bcryptjs')
const { Pool } = require('pg')
const logger = require('./logger')

const userDB = require('./database/user')
const clientDB = require('./database/client')
const addressesDB = require('./database/addresses')
const roleDB = require('./database/role')

const pool = new Pool({
    user: 'postgres_sso',
    password: 'password_sso',
    host: 'localhost',
    database: 'sso',
    port: 5432
})

pool.on('error', (err, client) => {
  logger.error(`Unexpected error on idle client: ${err}`)
  process.exit(2)
})

module.exports = {
    init: () => {
        const createTable = `
CREATE TABLE IF NOT EXISTS public.user (id SERIAL PRIMARY KEY, username TEXT UNIQUE NOT NULL, password TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS public.client (id SERIAL PRIMARY KEY, name TEXT UNIQUE NOT NULL, password TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS public.client_addresses (id SERIAL PRIMARY KEY, client_id INTEGER NOT NULL, ip TEXT NOT NULL, CONSTRAINT fk_addresses_client FOREIGN KEY(client_id) REFERENCES public.client(id));
CREATE TABLE IF NOT EXISTS public.roles (client_id INTEGER NOT NULL, user_id INTEGER NOT NULL, role TEXT NOT NULL, PRIMARY KEY(client_id, user_id, role), CONSTRAINT fk_roles_client FOREIGN KEY(client_id) REFERENCES public.client(id), CONSTRAINT fk_roles_user FOREIGN KEY(user_id) REFERENCES public.user(id))`
        pool.connect((err, client, done) => {
            if (err) {
                throw err
            }
            client.query(createTable, (err, res) => {
                done()
                if (err) {
                    logger.error(err.stack)
                }
            })
        })
    },
    findAddress: (ip, name, next) => addressesDB.find(pool, ip, name, next),
    findClients: (next) => clientDB.findAll(pool, next),
    findClient: (name, password, next) => clientDB.find(pool, name, password, next),
    addClient: (client, next) => clientDB.add(pool, client, next),
    findUsers: (next) => userDB.findAll(pool, next),
    findUser: (username, password, next) => userDB.find(pool, username, password, next),
    addUser: (user, next) => userDB.add(pool, user, next),
    updateUser: (user, next) => userDB.update(pool, user, next),
    addRole: (role, next) => roleDB.add(pool, role, next),
    findRoles: (next) => roleDB.findAll(pool, next)
}
