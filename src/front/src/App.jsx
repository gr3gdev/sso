import './App.css'

import React, { Component } from 'react'
import Login from './components/Login'
import ChangePassword from './components/ChangePassword'
import Admin from './components/Admin'
import httpClient from './httpClient'
import { Message } from 'semantic-ui-react'

export default class App extends Component {
    state = {
        connected : false,
        updated: null,
        error: null
    }
    componentDidMount = () => {
        httpClient.isConnected().then(data => this.setState({ 
            connected: data.success,
            updated: data.id,
            error: data.error
        }))
    }
    onSuccessLogin = () => {
        this.setState({
            connected : true,
            updated: null,
            error: null
        })
    }
    onSuccessUpdate = () => {
        this.setState({ 
            connected : false,
            updated: null,
            error: null
        })
    }
    render() {
        const { connected, updated, error } = this.state
        return (
            <div className='App'>
                { error ? <Message negative>{error}</Message> : null }
                { updated ? <ChangePassword id={updated} onSuccessUpdate={this.onSuccessUpdate} /> : null }
                { connected ? <Admin /> : <Login onSuccessLogin={this.onSuccessLogin} /> }
            </div>
        )
    }
}
