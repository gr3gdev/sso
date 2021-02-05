import './App.css'

import React, { Component } from 'react'
import Login from './components/Login'
import Admin from './components/Admin'
import httpClient from './httpClient'

export default class App extends Component {
    state = {
        connected : false
    }
    componentDidMount = () => {
        httpClient.isConnected().then(data => this.setState({ connected: data.success }))
    }
    onSuccessLogin = () => {
        this.setState({ connected : true })
    }
    render() {
        const { connected } = this.state
        return (
            <div className='App'>
                { connected ? <Admin /> : <Login onSuccessLogin={this.onSuccessLogin} /> }
            </div>
        )
    }
}
