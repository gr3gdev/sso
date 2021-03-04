import React from 'react'
import { Button, Form, Grid, Message, Segment } from 'semantic-ui-react'
import httpClient from '../httpClient'

export default class ChangePassword extends React.Component {
    state = { error: null, password: '', confirmPassword: '' }
    handleChangePassword = (e) => {
        this.setState({ password: e.target.value })
    }
    handleChangeConfirmPassword = (e) => {
        this.setState({ confirmPassword: e.target.value })
    }
	onFormSubmit = (evt) => {
		evt.preventDefault()
		const { password, confirmPassword } = this.state
        if (password === confirmPassword) {
            httpClient.saveUser({
                id: this.props.id,
                password: password
            }).then(res => {
                if (res.id > 0) {
                    this.props.onSuccessUpdate()
                } else {
                    this.setState({ error: 'Error when updating user' })
                }
            })
        } else {
            this.setState({
                error: 'Passwords are differents'
            })
        }
	}
	render() {
		const { error, password, confirmPassword } = this.state
		return (
            <Grid textAlign='center' style={{ height: '100vh' }} verticalAlign='middle'>
                <Grid.Column style={{ maxWidth: 350 }}>
                    <Form onSubmit={this.onFormSubmit.bind(this)} size='large'>
                        <Segment>
                            { error ? <Message negative>{error}</Message> : null }
                            <Form.Input fluid icon='lock' iconPosition='left' placeholder='Password' type='password' value={password} onChange={this.handleChangePassword} />
                            <Form.Input fluid icon='lock' iconPosition='left' placeholder='Confirm password' type='password' value={confirmPassword} onChange={this.handleChangeConfirmPassword} />
                            <Button type="submit">Submit</Button>
                        </Segment>
                    </Form>
                </Grid.Column>
            </Grid>
		)
	}
}
