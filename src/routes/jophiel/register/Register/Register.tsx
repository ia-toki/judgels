import * as React from 'react';
import { connect } from 'react-redux';

import RegisterForm, { RegisterFormData } from '../RegisterForm/RegisterForm';
import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/layouts/SingleColumnLayout/SingleColumnLayout';
import { AppState } from '../../../../modules/store';
import { selectRecaptchaSiteKey, selectUserRegistrationUseRecaptcha } from '../../modules/webConfigSelectors';
import { UserRegistrationData } from '../../../../modules/api/jophiel/user';
import { registerActions as injectedRegisterActions } from '../modules/registerActions';

export interface RegisterProps {
  onRegister: (data: RegisterFormData) => Promise<void>;
  useRecaptcha: boolean;
  recaptchaSiteKey?: string;
}

interface RegisterState {
  registeredUser?: {
    username: string;
    email: string;
  };
}

export class Register extends React.Component<RegisterProps, RegisterState> {
  state: RegisterState = {};

  render() {
    let content: JSX.Element;
    if (this.state.registeredUser) {
      content = (
        <Card title="Activation Required" className="card-register">
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
        useRecaptcha: this.props.useRecaptcha,
        recaptchaSiteKey: this.props.recaptchaSiteKey,
      };
      content = (
        <Card title="Register" className="card-register">
          <RegisterForm onSubmit={this.onRegister} {...registerFormProps} />
        </Card>
      );
    }

    return <SingleColumnLayout>{content}</SingleColumnLayout>;
  }

  private onRegister = async (data: RegisterFormData) => {
    await this.props.onRegister(data);
    this.setState({
      registeredUser: {
        username: data.username,
        email: data.email,
      },
    });
  };
}

export function createRegisterContainer(registerActions) {
  const mapStateToProps = (state: AppState) => ({
    useRecaptcha: selectUserRegistrationUseRecaptcha(state),
    recaptchaSiteKey: selectRecaptchaSiteKey(state),
  });

  const mapDispatchToProps = dispatch => ({
    onRegister: (data: RegisterFormData) => {
      const userRegistrationData: UserRegistrationData = {
        username: data.username,
        password: data.password,
        email: data.email,
        name: data.name,
        recaptchaResponse: data.recaptchaResponse,
      };
      return dispatch(registerActions.register(userRegistrationData));
    },
  });

  // https://github.com/DefinitelyTyped/DefinitelyTyped/issues/19989
  const RegisterWrapper = (props: RegisterProps) => <Register {...props} />;

  return connect(mapStateToProps, mapDispatchToProps)(RegisterWrapper);
}

const RegisterContainer = createRegisterContainer(injectedRegisterActions);
export default RegisterContainer;
