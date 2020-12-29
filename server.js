const express = require('express')
const path = require('path')
const bodyParser = require('body-parser')
const cookieParser = require('cookie-parser')

const logger = require('./logger')
const db = require('./database')
const bannerUtil = require('./utils/banner-util')

const indexRouter = require('./routes/index')
const loginRouter = require('./routes/login')
const adminRouter = require('./routes/admin')
const tokenRouter = require('./routes/token')

const app = express()
const port = process.env.PORT || 3000

app.set('view engine', 'pug')
app.set("views", path.join(__dirname, "views"))

app.use(bodyParser.urlencoded({ extended: true }))
app.use(bodyParser.json())
app.use(cookieParser())

app.use('/', indexRouter)
app.use('/login', loginRouter)
app.use('/admin', adminRouter)
app.use('/access-token', tokenRouter)

app.listen(port, () => {
    db.init()
    bannerUtil.load(() => logger.info(`Server started on port ${port}`))
})
