import * as React from 'react';
import { connect } from 'react-redux';

import ChangePasswordForm, { ChangePasswordFormData } from '../ChangePasswordForm/ChangePasswordForm';
import { Card } from '../../../../../../components/Card/Card';
import { changePasswordActions as injectedChangePasswordActions } from '../modules/changePasswordActions';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

export interface ChangePasswordPageProps {
  onChangePassword: (data: ChangePasswordFormData) => Promise<void>;
}

const ChangePasswordPage = (props: ChangePasswordPageProps) => (
  <Card title="Change password" className="card-change-password">
    <ChangePasswordForm onSubmit={props.onChangePassword} />
  </Card>
);

export function createChangePasswordPage(changePasswordActions) {
  const mapDispatchToProps = dispatch => ({
    onChangePassword: (data: ChangePasswordFormData) => {
      return dispatch(changePasswordActions.changePassword(data.oldPassword, data.password));
    },
  });

  return connect(undefined, mapDispatchToProps)(ChangePasswordPage);
}

export default withBreadcrumb('Change password')(createChangePasswordPage(injectedChangePasswordActions));
