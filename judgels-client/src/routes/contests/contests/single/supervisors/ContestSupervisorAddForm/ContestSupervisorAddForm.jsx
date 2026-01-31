import { Button, FormGroup, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormCheckbox } from '../../../../../../components/forms/FormCheckbox/FormCheckbox';
import { FormTextArea } from '../../../../../../components/forms/FormTextArea/FormTextArea';
import { Max100Lines, Required, composeValidators } from '../../../../../../components/forms/validations';
import { supervisorManagementPermissions } from '../../../../../../modules/api/uriel/contestSupervisor';

export default function ContestSupervisorAddForm({ onSubmit, renderFormComponents }) {
  const render = () => {
    const initialValues = { managementPermissions: { All: false } };
    return (
      <Form onSubmit={onSubmit} initialValues={initialValues}>
        {({ handleSubmit, values, submitting }) => (
          <form onSubmit={handleSubmit}>
            {renderFormComponents(renderFields(values), renderSubmitButton(submitting))}
          </form>
        )}
      </Form>
    );
  };

  const renderSubmitButton = submitting => {
    return <Button type="submit" text="Add/update" intent={Intent.PRIMARY} loading={submitting} />;
  };

  const renderFields = values => {
    return (
      <>
        {renderUsernamesField()}
        {renderPermissionFields(values)}
      </>
    );
  };

  const renderUsernamesField = () => {
    const usernamesField = {
      name: 'usernames',
      label: 'Usernames',
      labelHelper: '(one username per line, max 100 users)',
      rows: 8,
      isCode: true,
      validate: composeValidators(Required, Max100Lines),
      autoFocus: true,
    };

    return <Field component={FormTextArea} {...usernamesField} />;
  };

  const renderPermissionFields = values => {
    const allowAllPermissionsField = {
      name: 'managementPermissions.All',
      label: '(all)',
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
        {!values.managementPermissions.All &&
          permissionFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
      </FormGroup>
    );
  };

  return render();
}
