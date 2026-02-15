import { useState } from 'react';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { callAction } from '../../../../modules/callAction';
import ForgotPasswordForm from '../ForgotPasswordForm/ForgotPasswordForm';

import * as forgotPasswordActions from '../modules/forgotPasswordActions';

export default function ForgotPasswordPage() {
  const [state, setState] = useState({
    submitted: false,
  });

  const render = () => {
    let content;

    if (state.submitted) {
      content = (
        <div>
          <p data-key="instruction">An email has been sent to your email with instruction to reset your password.</p>
          <p>Please check your inbox/spam.</p>
        </div>
      );
    } else {
      content = <ForgotPasswordForm onSubmit={onForgetPassword} />;
    }
    return (
      <SingleColumnLayout>
        <Card title="Forgot password">{content}</Card>
      </SingleColumnLayout>
    );
  };

  const onForgetPassword = async data => {
    await callAction(forgotPasswordActions.requestToResetPassword(data.email));
    setState(prevState => ({ ...prevState, submitted: true }));
  };

  return render();
}
