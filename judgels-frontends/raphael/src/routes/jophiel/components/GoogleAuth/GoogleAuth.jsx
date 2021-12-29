import { Classes, Dialog } from '@blueprintjs/core';
import React from 'react';
import GoogleLogin from 'react-google-login';
import { connect } from 'react-redux';

import { APP_CONFIG } from '../../../../conf';
import GoogleAuthRegisterForm from '../GoogleAuthRegisterForm/GoogleAuthRegisterForm';
import * as googleAuthActions from '../../modules/googleAuthActions';

import './GoogleAuth.scss';

class GoogleAuth extends React.Component {
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
      <>
        <GoogleLogin
          clientId={APP_CONFIG.googleAuth.clientId}
          buttonText={this.state.isAuthorizing ? 'Continuing with Google...' : 'Continue with Google'}
          onSuccess={this.logIn}
          className="google-auth"
        />
        <hr />
        {this.renderDialog()}
      </>
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
    this.setState({
      idToken: response.tokenId,
      email: response.profileObj.email,
      isAuthorizing: true,
    });

    const isLoggedIn = await this.props.onLogIn(response);
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

const mapDispatchToProps = {
  onLogIn: googleAuthActions.logIn,
  onRegister: googleAuthActions.register,
};

export default connect(undefined, mapDispatchToProps)(GoogleAuth);
