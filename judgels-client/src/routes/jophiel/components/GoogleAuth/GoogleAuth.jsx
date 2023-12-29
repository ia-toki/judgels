import { Classes, Dialog } from '@blueprintjs/core';
import { GoogleLogin, GoogleOAuthProvider } from '@react-oauth/google';
import { Component } from 'react';
import { connect } from 'react-redux';

import { APP_CONFIG } from '../../../../conf';
import GoogleAuthRegisterForm from '../GoogleAuthRegisterForm/GoogleAuthRegisterForm';

import * as googleAuthActions from '../../modules/googleAuthActions';

import './GoogleAuth.scss';

class GoogleAuth extends Component {
  state = {
    email: undefined,
    idToken: undefined,
    isAuthorizing: false,
    isRegisterDialogOpen: false,
  };

  render() {
    if (!APP_CONFIG.googleAuth) {
      return null;
    }

    return (
      <GoogleOAuthProvider clientId={APP_CONFIG.googleAuth.clientId}>
        <GoogleLogin onSuccess={this.logIn} className="google-auth" />
        <hr />
        {this.renderDialog()}
      </GoogleOAuthProvider>
    );
  }

  toggleDialog = () => {
    this.setState(prevState => ({ isRegisterDialogOpen: !prevState.isRegisterDialogOpen }));
    this.props.onToggleInternalAuth();
  };

  closeDialog = () => {
    this.setState({ isAuthorizing: false });
    this.toggleDialog();
  };

  renderDialog = () => {
    return (
      <Dialog
        className="google-auth-dialog"
        isOpen={this.state.isRegisterDialogOpen}
        onClose={this.closeDialog}
        title="Register new account"
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        <GoogleAuthRegisterForm onSubmit={this.register} renderFormComponents={this.renderDialogForm} />
      </Dialog>
    );
  };

  renderDialogForm = (fields, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>
        <p>
          Welcome, <b>{this.state.email}</b>!
        </p>
        <p>Create a username for your account to complete the registration.</p>
        <hr />
        {fields}
      </div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>{submitButton}</div>
      </div>
    </>
  );

  logIn = async response => {
    const { credential } = response;
    const data = decodeJwtResponse(credential);

    this.setState({
      idToken: credential,
      email: data.email,
      isAuthorizing: true,
    });

    const isLoggedIn = await this.props.onLogIn(credential);
    if (!isLoggedIn) {
      this.toggleDialog();
    }
  };

  register = async data => {
    await this.props.onRegister({
      idToken: this.state.idToken,
      username: data.username,
    });
  };
}

function decodeJwtResponse(token) {
  var base64Url = token.split('.')[1];
  var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  var jsonPayload = decodeURIComponent(
    window
      .atob(base64)
      .split('')
      .map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      })
      .join('')
  );

  return JSON.parse(jsonPayload);
}

const mapDispatchToProps = {
  onLogIn: googleAuthActions.logIn,
  onRegister: googleAuthActions.register,
};

export default connect(undefined, mapDispatchToProps)(GoogleAuth);
