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
        const { username, password } = this.state
        httpClient.saveUser({
            username: username,
            password: password
        }).then(res => console.log(res))
    }
    handleEditUser = (user) => {
        this.setState({ userEdited: user })
    }
    handleDeleteUser = (user) => {
        console.log('TODO delete user')
    }
    handleSaveUser = () => {
        const { userEdited } = this.state
        httpClient.saveUser(userEdited).then(res => console.log(res))
        this.setState({ userEdited: null })
    }
	render() {
        const { clients, clientName, clientSecret, addressIP, users, username, password, clientEdited, userEdited } = this.state
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
                                    <Table.Cell><Input type='text' value={clientName} placeholder='Client name' /></Table.Cell>
                                    <Table.Cell><Input type='password' value={clientSecret} placeholder='Client secret' /></Table.Cell>
                                    <Table.Cell><Input type='text' value={addressIP} placeholder='0.0.0.0' /></Table.Cell>
                                    <Table.Cell>
                                        <Button icon='add' onClick={this.handleAddClient} />
                                    </Table.Cell>
                                </Table.Row>
                                {clients.map((client, index) => {
                                    if (clientEdited) {
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
                                    <Table.Cell><Input type='text' value={username} placeholder='User name' /></Table.Cell>
                                    <Table.Cell><Input type='password' value={password} placeholder='User password' /></Table.Cell>
                                    <Table.Cell>TODO Clients</Table.Cell>
                                    <Table.Cell>
                                        <Button icon='add' onClick={this.handleAddUser} />
                                    </Table.Cell>
                                </Table.Row>
                                {users.map((user, index) => {
                                    if (userEdited) {
                                        return (
                                            <Table.Row key={index}>
                                                <Table.Cell><Input type='text' value={userEdited.username} placeholder='User name' /></Table.Cell>
                                                <Table.Cell><Input type='password' value={userEdited.password} placeholder='User password' /></Table.Cell>
                                                <Table.Cell>TODO Clients</Table.Cell>
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
                                                <Table.Cell>TODO Clients</Table.Cell>
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
