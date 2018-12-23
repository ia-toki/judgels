import { Button, Intent, HTMLTable } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required } from 'components/forms/validations';
import { FormTextArea } from 'components/forms/FormTextArea/FormTextArea';
import { FormTableInput } from 'components/forms/FormTableInput/FormTableInput';
import { FormCheckbox } from 'components/forms/FormCheckbox/FormCheckbox';
import { supervisorPermissionsList } from 'modules/api/uriel/contestSupervisor';

export interface ContestSupervisorAddFormData {
  usernames: string;
  managementPermissions?: { [key: string]: boolean };
}

export interface ContestSupervisorAddFormProps extends InjectedFormProps<ContestSupervisorAddFormData> {
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

class ContestSupervisorAddForm extends React.Component<ContestSupervisorAddFormProps> {
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
      <HTMLTable>
        <tbody>
          <tr>
            <td colSpan={2}> {this.renderUsernamesField()} </td>
          </tr>
          {this.renderPermissionsForm()}
        </tbody>
      </HTMLTable>
    );
  }

  private renderUsernamesField() {
    const usernamesField: any = {
      name: 'usernames',
      label: 'Usernames',
      labelHelper: '(one username per line, max 100 users)',
      rows: 8,
      validate: [Required],
      autoFocus: true,
    };

    return <Field component={FormTextArea} {...usernamesField} />;
  }

  private renderPermissionsForm() {
    const permissionsField: any = {
      label: 'Permissions',
      meta: {},
    };

    const permissionsProps = supervisorPermissionsList.map(
      p =>
        ({
          name: 'managementPermissions.' + p,
          label: p,
          small: true,
        } as any)
    );

    return (
      <FormTableInput {...permissionsField}>
        {permissionsProps.map(p => <Field key={p.name} component={FormCheckbox} {...p} />)}
      </FormTableInput>
    );
  }
}

export default reduxForm<ContestSupervisorAddFormData>({
  form: 'contest-supervisor-add',
  initialValues: {
    managementPermissions: supervisorPermissionsList.reduce((obj, p) => {
      obj[p] = true;
      return obj;
    }, {}),
  },
  touchOnBlur: false,
})(ContestSupervisorAddForm);
