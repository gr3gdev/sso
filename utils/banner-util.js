const fs = require('fs')
const readline = require('readline')

module.exports = {
    load: (next) => {
        const lineReader = readline.createInterface({
            input: fs.createReadStream('banner.txt')
        })
        lineReader.on('line', (line) => {
            console.log("\x1b[32m", line)
        })
        lineReader.on('close', () => {
            next()
        })
    }
}