import { Button, FormGroup, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required, Max100Lines } from 'components/forms/validations';
import { FormTextArea } from 'components/forms/FormTextArea/FormTextArea';
import { FormCheckbox } from 'components/forms/FormCheckbox/FormCheckbox';
import { supervisorManagementPermissions } from 'modules/api/uriel/contestSupervisor';

export interface ContestSupervisorAddFormData {
  usernames: string;
  managementPermissions?: { [key: string]: boolean };
}

export interface ContestSupervisorAddFormProps extends InjectedFormProps<ContestSupervisorAddFormData> {
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

interface ContestSupervisorAddFormState {
  allowAllPermissions?: boolean;
}

class ContestSupervisorAddForm extends React.Component<ContestSupervisorAddFormProps, ContestSupervisorAddFormState> {
  state: ContestSupervisorAddFormState = {};

  render() {
    return (
      <form onSubmit={this.props.handleSubmit}>
        {this.props.renderFormComponents(this.renderFields(), this.renderSubmitButton())}
      </form>
    );
  }

  private renderSubmitButton() {
    return <Button type="submit" text="Add/update" intent={Intent.PRIMARY} loading={this.props.submitting} />;
  }

  private renderFields() {
    return (
      <>
        {this.renderUsernamesField()}
        {this.renderPermissionFields()}
      </>
    );
  }

  private renderUsernamesField() {
    const usernamesField: any = {
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

  private renderPermissionFields() {
    const allowAllPermissionsField: any = {
      name: 'managementPermissions.All',
      label: '(all)',
      onChange: this.toggleAllowAllPermissionsCheckbox,
    };

    const permissionFields = supervisorManagementPermissions.filter(p => p !== 'All').map(
      p =>
        ({
          name: 'managementPermissions.' + p,
          label: p,
          small: true,
        } as any)
    );

    return (
      <FormGroup label="Management permissions">
        <Field component={FormCheckbox} {...allowAllPermissionsField} />
        {!this.state.allowAllPermissions &&
          permissionFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
      </FormGroup>
    );
  }

  private toggleAllowAllPermissionsCheckbox = (e, checked) => {
    this.setState({ allowAllPermissions: checked });
  };
}

export default reduxForm<ContestSupervisorAddFormData>({
  form: 'contest-supervisor-add',
  touchOnBlur: false,
})(ContestSupervisorAddForm);
