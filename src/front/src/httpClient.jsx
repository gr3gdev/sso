import axios from 'axios'

const httpClient = axios.create()

httpClient.isConnected = function() {
    const token = localStorage.getItem('MySSO_token')
    if (token) {
        httpClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
    return httpClient.logIn()
}

httpClient.logIn = function(credentials) {
	return this({ method: 'post', url: `/login`, data: credentials })
		.then((serverResponse) => {
            const token = serverResponse.data.token
            if (token) {
                if (serverResponse.data.mustUpdated === true) {
                    return {
                        success: false,
                        update: serverResponse.data.id,
                        error: null
                    }
                } else {
                    localStorage.setItem('MySSO_token', token)
                    httpClient.defaults.headers.common['Authorization'] = `Bearer ${token}`
                    return {
                        success: true,
                        update: null,
                        error: null
                    }
                }
            } else {
                localStorage.removeItem('MySSO_token')
                return {
                    success: false,
                    update: null,
                    error: null
                }
            }
		})
		.catch((err) => {
            localStorage.removeItem('MySSO_token')
            return {
                success: false,
                error: 'Authentification failed'
            }
		})
}

httpClient.getClients = function() {
    return this({ method: 'get', url: `/admin/clients` })
        .then((serverResponse) => {
            return serverResponse.data
        })
}

httpClient.saveClient = function(client) {
    return this({ method: 'put', url: `/admin/clients`, data: client })
        .then((serverResponse) => {
            return serverResponse.data
        })
}

httpClient.getUsers = function() {
    return this({ method: 'get', url: `/admin/users` })
        .then((serverResponse) => {
            return serverResponse.data
        })
}

httpClient.saveUser = function(user) {
    return this({ method: 'put', url: `/admin/users`, data: user })
        .then((serverResponse) => {
            return serverResponse.data
        })
}

export default httpClient
