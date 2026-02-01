import { useState } from 'react';
import { useDispatch } from 'react-redux';

import { Card } from '../../../../../components/Card/Card';
import ResetPasswordForm from '../ResetPasswordForm/ResetPasswordForm';

import * as resetPasswordActions from '../modules/resetPasswordActions';

export default function ResetPasswordPage() {
  const dispatch = useDispatch();

  const [state, setState] = useState({
    submitted: false,
  });

  const render = () => {
    return <Card title="Reset password">{renderContent()}</Card>;
  };

  const renderContent = () => {
    if (state.submitted) {
      return (
        <div>
          <p data-key="instruction">An email has been sent to your email with instruction to reset your password.</p>
          <p>Please check your inbox/spam.</p>
        </div>
      );
    }
    return <ResetPasswordForm onSubmit={resetPassword} />;
  };

  const resetPassword = async () => {
    await dispatch(resetPasswordActions.requestToResetPassword());
    setState(prevState => ({ ...prevState, submitted: true }));
  };

  return render();
}
