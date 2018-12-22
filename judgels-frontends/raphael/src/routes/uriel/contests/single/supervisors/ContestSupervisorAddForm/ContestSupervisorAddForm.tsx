import { Button, Intent, HTMLTable } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required } from 'components/forms/validations';
import { FormTextArea } from 'components/forms/FormTextArea/FormTextArea';
import { FormTableInput } from 'components/forms/FormTableInput/FormTableInput';
import { FormCheckbox } from 'components/forms/FormCheckbox/FormCheckbox';
import { SupervisorManagementPermission } from 'modules/api/uriel/contestSupervisor';

export interface ContestSupervisorAddFormData {
  usernames: string;
  grantAllPermissions?: boolean;
  grantedPermissions?: { [key: string]: boolean };
}

export interface ContestSupervisorAddFormProps extends InjectedFormProps<ContestSupervisorAddFormData> {
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

interface ContestSupervisorAddFormState {
  grantAllPermissions: boolean;
}

class ContestSupervisorAddForm extends React.Component<ContestSupervisorAddFormProps, ContestSupervisorAddFormState> {
  state: ContestSupervisorAddFormState;

  constructor(props: ContestSupervisorAddFormProps) {
    super(props);
    this.state = { grantAllPermissions: false };
  }

  render() {
    return (
      <form onSubmit={this.props.handleSubmit}>
        {this.props.renderFormComponents(this.renderFields(), this.renderSubmitButton())}
      </form>
    );
  }

  private toggleAllowAllLanguagesCheckbox = (e, checked) => {
    this.setState({ grantAllPermissions: checked });
  };

  private renderSubmitButton() {
    return <Button type="submit" text="Add/update" intent={Intent.PRIMARY} loading={this.props.submitting} />;
  }

  private renderFields() {
    return (
      <HTMLTable>
        <tbody>
          <tr>
            <td colSpan={2}> {this.renderUsernamesField()} </td>
          </tr>
          {this.renderPrivilegesForm()}
        </tbody>
      </HTMLTable>
    );
  }

  private renderUsernamesField() {
    const usernamesField: any = {
      name: 'usernames',
      label: 'Usernames',
      labelHelper: '(one username per line, max 100 users)',
      rows: 5,
      validate: [Required],
      autoFocus: true,
    };

    return <Field component={FormTextArea} {...usernamesField} />;
  }

  private renderPrivilegesForm() {
    const permissionsField: any = {
      label: 'Permissions',
      meta: {},
    };

    const grantAllPermissionsField: any = {
      name: 'grantAllPermissions',
      label: 'All',
      onChange: this.toggleAllowAllLanguagesCheckbox,
    };

    const privileges = Object.keys(SupervisorManagementPermission).filter(e => e !== 'All');

    const privilegesProps = privileges.map(
      p =>
        ({
          name: 'grantedPermissions.' + p,
          label: p,
          small: true,
        } as any)
    );

    return (
      <FormTableInput {...permissionsField}>
        <Field component={FormCheckbox} {...grantAllPermissionsField} />
        {!this.state.grantAllPermissions &&
          privilegesProps.map(p => <Field key={p.name} component={FormCheckbox} {...p} />)}
      </FormTableInput>
    );
  }
}

export default reduxForm<ContestSupervisorAddFormData>({
  form: 'contest-supervisor-add',
  touchOnBlur: false,
})(ContestSupervisorAddForm);
