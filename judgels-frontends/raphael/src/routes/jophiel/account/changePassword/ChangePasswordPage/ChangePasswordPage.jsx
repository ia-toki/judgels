import { connect } from 'react-redux';

import { Card } from '../../../../../components/Card/Card';
import { withBreadcrumb } from '../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ChangePasswordForm from '../ChangePasswordForm/ChangePasswordForm';
import * as changePasswordActions from '../modules/changePasswordActions';

function ChangePasswordPage({ onUpdateMyPassword }) {
  return (
    <Card title="Change password" className="card-change-password">
      <ChangePasswordForm onSubmit={onUpdateMyPassword} />
    </Card>
  );
}

const mapDispatchToProps = {
  onUpdateMyPassword: data => changePasswordActions.updateMyPassword(data.oldPassword, data.password),
};

export default withBreadcrumb('Change password')(connect(undefined, mapDispatchToProps)(ChangePasswordPage));
