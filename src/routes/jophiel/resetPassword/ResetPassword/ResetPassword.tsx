import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { Card } from '../../../../components/Card/Card';
import ResetPasswordForm, { ResetPasswordFormData } from '../ResetPasswordForm/ResetPasswordForm';
import { SingleColumnLayout } from 'components/layouts/SingleColumnLayout/SingleColumnLayout';
import { resetPasswordActions as injectedResetPasswordActions } from '../modules/resetPasswordActions';

export interface ResetPasswordProps {
  onResetPassword: (data: ResetPasswordFormData) => Promise<void>;
}

export const ResetPassword = (props: ResetPasswordProps) => (
  <SingleColumnLayout>
    <Card title="Reset password">
      <ResetPasswordForm onSubmit={props.onResetPassword} />
    </Card>
  </SingleColumnLayout>
);

interface ResetPasswordContainerProps {
  match: {
    params: {
      emailCode: string;
    };
  };

  onResetPassword: (emailCode: string, data: ResetPasswordFormData) => Promise<void>;
}

const ResetPasswordContainer = (props: ResetPasswordContainerProps) => {
  const onResetPassword = (data: ResetPasswordFormData) => props.onResetPassword(props.match.params.emailCode, data);
  return <ResetPassword onResetPassword={onResetPassword} />;
};

export function createResetPasswordContainer(resetPasswordActions) {
  const mapDispatchToProps = dispatch => ({
    onResetPassword: (emailCode: string, data: ResetPasswordFormData) => {
      return dispatch(resetPasswordActions.reset(emailCode, data.password));
    },
  });

  return withRouter<any>(connect(undefined, mapDispatchToProps)(ResetPasswordContainer));
}

export default createResetPasswordContainer(injectedResetPasswordActions);
