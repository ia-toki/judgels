import * as React from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../../components/Card/Card';
import { withBreadcrumb } from '../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ChangePasswordForm, { ChangePasswordFormData } from '../ChangePasswordForm/ChangePasswordForm';
import * as changePasswordActions from '../modules/changePasswordActions';

export interface ChangePasswordPageProps {
  onUpdateMyPassword: (data: ChangePasswordFormData) => Promise<void>;
}

const ChangePasswordPage = (props: ChangePasswordPageProps) => (
  <Card title="Change password" className="card-change-password">
    <ChangePasswordForm onSubmit={props.onUpdateMyPassword} />
  </Card>
);

const mapDispatchToProps = {
  onUpdateMyPassword: (data: ChangePasswordFormData) =>
    changePasswordActions.updateMyPassword(data.oldPassword, data.password),
};

export default withBreadcrumb('Change password')(connect(undefined, mapDispatchToProps)(ChangePasswordPage));
