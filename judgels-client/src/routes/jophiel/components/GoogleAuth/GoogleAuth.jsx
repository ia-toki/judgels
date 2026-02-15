import { Classes, Dialog } from '@blueprintjs/core';
import { GoogleLogin, GoogleOAuthProvider } from '@react-oauth/google';
import { useState } from 'react';

import { APP_CONFIG } from '../../../../conf';
import { callAction } from '../../../../modules/callAction';
import GoogleAuthRegisterForm from '../GoogleAuthRegisterForm/GoogleAuthRegisterForm';

import * as googleAuthActions from '../../modules/googleAuthActions';

import './GoogleAuth.scss';

export default function GoogleAuth({ onToggleInternalAuth }) {
  const [state, setState] = useState({
    email: undefined,
    idToken: undefined,
    isAuthorizing: false,
    isRegisterDialogOpen: false,
  });

  const render = () => {
    if (!APP_CONFIG.googleAuth) {
      return null;
    }

    return (
      <GoogleOAuthProvider clientId={APP_CONFIG.googleAuth.clientId}>
        <GoogleLogin onSuccess={logIn} className="google-auth" />
        <hr />
        {renderDialog()}
      </GoogleOAuthProvider>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isRegisterDialogOpen: !prevState.isRegisterDialogOpen }));
    onToggleInternalAuth();
  };

  const closeDialog = () => {
    setState(prevState => ({ ...prevState, isAuthorizing: false }));
    toggleDialog();
  };

  const renderDialog = () => {
    return (
      <Dialog
        className="google-auth-dialog"
        isOpen={state.isRegisterDialogOpen}
        onClose={closeDialog}
        title="Register new account"
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        <GoogleAuthRegisterForm onSubmit={register} renderFormComponents={renderDialogForm} />
      </Dialog>
    );
  };

  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>
        <p>
          Welcome, <b>{state.email}</b>!
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

  const logIn = async response => {
    const { credential } = response;
    const data = decodeJwtResponse(credential);

    setState(prevState => ({
      ...prevState,
      idToken: credential,
      email: data.email,
      isAuthorizing: true,
    }));

    const isLoggedIn = await callAction(googleAuthActions.logIn(credential));
    if (!isLoggedIn) {
      toggleDialog();
    }
  };

  const register = async data => {
    await callAction(
      googleAuthActions.register({
        idToken: state.idToken,
        username: data.username,
      })
    );
  };

  return render();
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
