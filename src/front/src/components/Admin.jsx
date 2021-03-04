import React from 'react'
import { Button, Grid, Input, Message, Tab, Table } from 'semantic-ui-react'
import httpClient from '../httpClient'

export default class Admin extends React.Component {
    state = { 
        clients: [],
        clientName: '',
        clientSecret: '',
        addressIP: '',
        users: [],
        username: '',
        password: '',
        userClients: '',
        clientEdited: null,
        userEdited: null
    }
    componentDidMount = () => {
        httpClient.getClients().then(res => this.setState({ clients : res }))
        httpClient.getUsers().then(res => this.setState({ users : res }))
    }
    handleAddClient = () => {
        const { clientName, clientSecret, addressIP } = this.state
        httpClient.saveClient({
            clientName: clientName,
            clientSecret: clientSecret,
            addressIP: addressIP
        }).then(res => console.log(res))
    }
    handleEditClient = (client) => {
        this.setState({ clientEdited: client })
    }
    handleDeleteClient = (client) => {
        console.log('TODO delete client')
    }
    handleSaveClient = () => {
        const { clientEdited } = this.state
        httpClient.saveClient(clientEdited).then(res => console.log(res))
        this.setState({ clientEdited: null })
    }
    handleAddUser = () => {
        const { username, password, userClients } = this.state
        const clients = {}
        userClients.split(',').forEach(c => clients[c] = "USER")
        httpClient.saveUser({
            username: username,
            password: password,
            clients: clients
        }).then(res => console.log(res))
    }
    handleEditUser = (user) => {
        const edited = user
        edited.userClients = Object.keys(user.clients).join(',')
        this.setState({ userEdited: edited })
    }
    handleDeleteUser = (user) => {
        console.log('TODO delete user')
    }
    handleSaveUser = () => {
        const { userEdited } = this.state
        const clients = {}
        userEdited.userClients.split(',').forEach(c => clients[c] = "USER")
        httpClient.saveUser({
            username: userEdited.username,
            password: userEdited.password,
            clients: clients
        }).then(res => console.log(res))
        this.setState({ userEdited: null })
    }
    handleChangeClientName = (e) => this.setState({ clientName: e.target.value })
    handleChangeClientSecret = (e) => this.setState({ clientSecret: e.target.value })
    handleChangeClientIP = (e) => this.setState({ addressIP: e.target.value })
    handleChangeUserName = (e) => this.setState({ username: e.target.value })
    handleChangeUserPassword = (e) => this.setState({ password: e.target.value })
    handleChangeUserClient = (e) => this.setState({ userClients: e.target.value })
	render() {
        const { clients, clientName, clientSecret, addressIP, users, username, password, userClients, clientEdited, userEdited } = this.state
        const panes = [
            {
                menuItem: 'Clients',
                render: () => (
                    <Tab.Pane>
                        <Table celled selectable>
                            <Table.Header>
                                <Table.HeaderCell>Name</Table.HeaderCell>
                                <Table.HeaderCell>Secret</Table.HeaderCell>
                                <Table.HeaderCell>Address</Table.HeaderCell>
                                <Table.HeaderCell>Actions</Table.HeaderCell>
                            </Table.Header>
                            <Table.Body>
                                <Table.Row>
                                    <Table.Cell><Input type='text' value={clientName} onChange={this.handleChangeClientName} placeholder='Client name' /></Table.Cell>
                                    <Table.Cell><Input type='password' value={clientSecret} onChange={this.handleChangeClientSecret} placeholder='Client secret' /></Table.Cell>
                                    <Table.Cell><Input type='text' value={addressIP} onChange={this.handleChangeClientIP} placeholder='0.0.0.0' /></Table.Cell>
                                    <Table.Cell>
                                        <Button icon='add' onClick={this.handleAddClient} />
                                    </Table.Cell>
                                </Table.Row>
                                {clients.map((client, index) => {
                                    if (clientEdited.id === client.id) {
                                        return (
                                            <Table.Row key={index}>
                                                <Table.Cell><Input type='text' value={clientEdited.clientName} placeholder='Client name' /></Table.Cell>
                                                <Table.Cell><Input type='password' value={clientEdited.clientSecret} placeholder='Client secret' /></Table.Cell>
                                                <Table.Cell><Input type='text' value={clientEdited.addressIP} placeholder='0.0.0.0' /></Table.Cell>
                                                <Table.Cell>
                                                    <Button icon='save' onClick={this.handleSaveClient} />
                                                </Table.Cell>
                                            </Table.Row>
                                        )
                                    } else {
                                        return (
                                            <Table.Row key={index}>
                                                <Table.Cell>{client.clientName}</Table.Cell>
                                                <Table.Cell>********</Table.Cell>
                                                <Table.Cell>{client.addressIP}</Table.Cell>
                                                <Table.Cell>
                                                    <Button icon='edit' onClick={() => this.handleEditClient(client)} />
                                                    <Button icon='delete' onClick={() => this.handleDeleteClient(client)} />
                                                </Table.Cell>
                                            </Table.Row>
                                        )
                                    }
                                })}
                            </Table.Body>
                        </Table>
                    </Tab.Pane>
                )
            },
            {
                menuItem: 'Users',
                render: () => (
                    <Tab.Pane>
                        <Table celled selectable>
                            <Table.Header>
                                <Table.HeaderCell>Name</Table.HeaderCell>
                                <Table.HeaderCell>Password</Table.HeaderCell>
                                <Table.HeaderCell>Clients</Table.HeaderCell>
                                <Table.HeaderCell>Actions</Table.HeaderCell>
                            </Table.Header>
                            <Table.Body>
                                <Table.Row>
                                    <Table.Cell><Input type='text' value={username} onChange={this.handleChangeUserName} placeholder='User name' /></Table.Cell>
                                    <Table.Cell><Input type='password' value={password} onChange={this.handleChangeUserPassword} placeholder='User password' /></Table.Cell>
                                    <Table.Cell><Input type='text' value={userClients} onChange={this.handleChangeUserClient} placeholder='Clients name' /></Table.Cell>
                                    <Table.Cell>
                                        <Button icon='add' onClick={this.handleAddUser} />
                                    </Table.Cell>
                                </Table.Row>
                                {users.map((user, index) => {
                                    if (userEdited.id === user.id) {
                                        return (
                                            <Table.Row key={index}>
                                                <Table.Cell><Input type='text' value={userEdited.username} placeholder='User name' /></Table.Cell>
                                                <Table.Cell><Input type='password' value={userEdited.password} placeholder='User password' /></Table.Cell>
                                                <Table.Cell><Input type='text' value={userEdited.userClients} placeholder='Clients name' /></Table.Cell>
                                                <Table.Cell>
                                                    <Button icon='save' onClick={this.handleSaveUser} />
                                                </Table.Cell>
                                            </Table.Row>
                                        )
                                    } else {
                                        return (
                                            <Table.Row key={index}>
                                                <Table.Cell>{user.username}</Table.Cell>
                                                <Table.Cell>********</Table.Cell>
                                                <Table.Cell>{Object.keys(user.clients).join(',')}</Table.Cell>
                                                <Table.Cell>
                                                    <Button icon='edit' onClick={() => this.handleEditUser(user)} />
                                                    <Button icon='delete' onClick={() => this.handleDeleteUser(user)} />
                                                </Table.Cell>
                                            </Table.Row>
                                        )
                                    }
                                })}
                            </Table.Body>
                        </Table>
                    </Tab.Pane>
                )
            }
        ]
        return (
            <Grid textAlign='center' style={{ height: '100vh' }} verticalAlign='top'>
                <Grid.Column>
                    <Tab panes={panes} />
                </Grid.Column>
            </Grid>
        )
	}
}
