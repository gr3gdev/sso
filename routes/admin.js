const adminRouter = require('express').Router()
const logger = require('../logger')
const db = require('../database')

const render = (res) => {
    db.findClients((clients) => {
        db.findUsers((users) => {
            db.findRoles((roles) => {
                res.render('admin', {
                    clients: clients,
                    users: users,
                    roles: roles
                })
            })
        })
    })
}

adminRouter.route('/')
    .get((req, res) => {
        logger.info('Access admin page')
        render(res)
    })
    .post((req, res) => {
        const datas = req.body
        if (datas.action === 'addClient') {
            db.addClient({
                name: datas.clientName,
                password: datas.clientPassword,
                ips: datas.clientIPS.split(',')
            }, () => render(res))
        } else if (datas.action === 'addUser') {
            db.addUser({
                username: datas.userName,
                password: datas.userPassword
            }, () => render(res))
        } else if (datas.action === 'addRole') {
            db.addRole({
                client: {
                    id: datas.clientId
                },
                user: {
                    id: datas.userId
                },
                name: datas.roleName
            }, () => render(res))
        } else {
            render(res)
        }
    })

module.exports = adminRouter