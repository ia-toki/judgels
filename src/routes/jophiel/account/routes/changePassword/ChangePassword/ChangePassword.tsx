import * as React from 'react';
import { connect } from 'react-redux';

import ChangePasswordForm, { ChangePasswordFormData } from '../ChangePasswordForm/ChangePasswordForm';
import { Card } from '../../../../../../components/Card/Card';
import { changePasswordActions as injectedChangePasswordActions } from '../modules/changePasswordActions';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

export interface ChangePasswordProps {
  onChangePassword: (data: ChangePasswordFormData) => Promise<void>;
}

export const ChangePassword = (props: ChangePasswordProps) => (
  <Card title="Change password" className="card-change-password">
    <ChangePasswordForm onSubmit={props.onChangePassword} />
  </Card>
);

export function createChangePasswordContainer(changePasswordActions) {
  const mapDispatchToProps = dispatch => ({
    onChangePassword: (data: ChangePasswordFormData) => {
      return dispatch(changePasswordActions.changePassword(data.oldPassword, data.password));
    },
  });

  return connect(undefined, mapDispatchToProps)(ChangePassword);
}

const ChangePasswordContainer = withBreadcrumb('Change password')(
  createChangePasswordContainer(injectedChangePasswordActions)
);
export default ChangePasswordContainer;
