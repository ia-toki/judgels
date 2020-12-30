import { Button, FormGroup, Intent } from '@blueprintjs/core';
import { Component } from 'react';
import { Field, reduxForm } from 'redux-form';

import { Required, Max100Lines } from '../../../../../../components/forms/validations';
import { FormTextArea } from '../../../../../../components/forms/FormTextArea/FormTextArea';
import { FormCheckbox } from '../../../../../../components/forms/FormCheckbox/FormCheckbox';
import { supervisorManagementPermissions } from '../../../../../../modules/api/uriel/contestSupervisor';

class ContestSupervisorAddForm extends Component {
  state = {
    allowAllPermissions: false,
  };

  render() {
    return (
      <form onSubmit={this.props.handleSubmit}>
        {this.props.renderFormComponents(this.renderFields(), this.renderSubmitButton())}
      </form>
    );
  }

  renderSubmitButton() {
    return <Button type="submit" text="Add/update" intent={Intent.PRIMARY} loading={this.props.submitting} />;
  }

  renderFields() {
    return (
      <>
        {this.renderUsernamesField()}
        {this.renderPermissionFields()}
      </>
    );
  }

  renderUsernamesField() {
    const usernamesField = {
      name: 'usernames',
      label: 'Usernames',
      labelHelper: '(one username per line, max 100 users)',
      rows: 8,
      isCode: true,
      validate: [Required, Max100Lines],
      autoFocus: true,
    };

    return <Field component={FormTextArea} {...usernamesField} />;
  }

  renderPermissionFields() {
    const allowAllPermissionsField = {
      name: 'managementPermissions.All',
      label: '(all)',
      onChange: this.toggleAllowAllPermissionsCheckbox,
    };

    const permissionFields = supervisorManagementPermissions
      .filter(p => p !== 'All')
      .map(p => ({
        name: 'managementPermissions.' + p,
        label: p,
        small: true,
      }));

    return (
      <FormGroup label="Management permissions">
        <Field component={FormCheckbox} {...allowAllPermissionsField} />
        {!this.state.allowAllPermissions &&
          permissionFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
      </FormGroup>
    );
  }

  toggleAllowAllPermissionsCheckbox = (e, checked) => {
    this.setState({ allowAllPermissions: checked });
  };
}

export default reduxForm({
  form: 'contest-supervisor-add',
  touchOnBlur: false,
})(ContestSupervisorAddForm);
