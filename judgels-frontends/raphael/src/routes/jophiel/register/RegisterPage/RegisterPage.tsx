import * as React from 'react';
import { connect } from 'react-redux';

import { Card } from 'components/Card/Card';
import { SingleColumnLayout } from 'components/SingleColumnLayout/SingleColumnLayout';
import { UserRegistrationData } from 'modules/api/jophiel/userAccount';
import { UserRegistrationWebConfig } from 'modules/api/jophiel/userRegistration';

import RegisterForm, { RegisterFormData } from '../RegisterForm/RegisterForm';
import { registerActions as injectedRegisterActions } from '../modules/registerActions';

import './RegisterPage.css';

export interface RegisterPageProps {
  onGetWebConfig: () => Promise<UserRegistrationWebConfig>;
  onRegisterUser: (data: RegisterFormData) => Promise<void>;
}

interface RegisterPageState {
  config?: UserRegistrationWebConfig;
  registeredUser?: {
    username: string;
    email: string;
  };
}

class RegisterPage extends React.PureComponent<RegisterPageProps, RegisterPageState> {
  state: RegisterPageState = {};

  async componentDidMount() {
    const config = await this.props.onGetWebConfig();
    this.setState({ config });
  }

  render() {
    const { config } = this.state;
    if (!config) {
      return null;
    }

    let content: JSX.Element;
    if (this.state.registeredUser) {
      content = (
        <Card title="Activation required" className="card-register">
          <p>
            Thank you for registering, <strong>{this.state.registeredUser.username}</strong>.
          </p>
          <p data-key="instruction" className="card-register__instruction">
            A confirmation email has been sent to&nbsp;
            <strong>{this.state.registeredUser.email}</strong> with instruction to activate your account.
          </p>
          <p>Please check your inbox/spam.</p>
        </Card>
      );
    } else {
      const registerFormProps = {
        useRecaptcha: config.useRecaptcha,
        recaptchaSiteKey: config.recaptcha && config.recaptcha.siteKey,
      };
      content = (
        <Card title="Register" className="card-register">
          <RegisterForm onSubmit={this.onRegisterUser} {...registerFormProps} />
        </Card>
      );
    }

    return <SingleColumnLayout>{content}</SingleColumnLayout>;
  }

  private onRegisterUser = async (data: RegisterFormData) => {
    await this.props.onRegisterUser(data);
    this.setState({
      registeredUser: {
        username: data.username,
        email: data.email,
      },
    });
  };
}

export function createRegisterPage(registerActions) {
  const mapDispatchToProps = {
    onGetWebConfig: registerActions.getWebConfig,
    onRegisterUser: (data: RegisterFormData) => {
      const userRegistrationData: UserRegistrationData = {
        username: data.username,
        password: data.password,
        email: data.email,
        name: data.name,
        recaptchaResponse: data.recaptchaResponse,
      };
      return registerActions.registerUser(userRegistrationData);
    },
  };

  return connect(undefined, mapDispatchToProps)(RegisterPage);
}

export default createRegisterPage(injectedRegisterActions);
