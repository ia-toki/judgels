import { Button, Callout, Classes, Dialog, FormGroup, Intent } from '@blueprintjs/core';
import { Upload } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { Required } from '../../../../components/forms/validations';
import { upsertUserRolesMutationOptions } from '../../../../modules/queries/userRole';
import { RoleCheckboxes } from '../RoleCheckboxes/RoleCheckboxes';

import * as toastActions from '../../../../modules/toast/toastActions';

import './RoleUpsertDialog.scss';

const usernamesField = {
  name: 'usernames',
  label: 'Usernames',
  labelHelper: '(one username per line)',
  rows: 6,
  isCode: true,
  validate: Required,
  autoFocus: true,
};

const initialValues = { roles: {} };

function parseUsernames(text) {
  const usernames = text
    .split('\n')
    .map(s => s.trim())
    .filter(s => s.length > 0);
  return [...new Set(usernames)];
}

export function RoleUpsertDialog({ superadminUsername }) {
  const [isOpen, setIsOpen] = useState(false);
  const [error, setError] = useState(undefined);

  const upsertRolesMutation = useMutation(upsertUserRolesMutationOptions());

  const openDialog = () => {
    setError(undefined);
    setIsOpen(true);
  };

  const closeDialog = () => {
    setIsOpen(false);
  };

  const upsertUsers = async (values, form) => {
    const usernames = parseUsernames(values.usernames);
    if (usernames.includes(superadminUsername)) {
      setError(`${superadminUsername} already holds every role.`);
      return;
    }

    const role = Object.fromEntries(
      Object.entries(values.roles)
        .filter(([, checked]) => checked)
        .map(([key]) => [key, 'ADMIN'])
    );
    const { upsertedUserProfilesMap } = await upsertRolesMutation.mutateAsync({ usernames, role });

    const notFound = usernames.filter(username => !upsertedUserProfilesMap[username]);
    if (notFound.length < usernames.length) {
      toastActions.showSuccessToast('Roles granted.');
    }
    if (notFound.length === 0) {
      setIsOpen(false);
    } else {
      // Leave only the usernames that still need fixing.
      form.change('usernames', notFound.join('\n'));
      setError(`User not found: ${notFound.join(', ')}.`);
    }
  };

  return (
    <>
      <Button intent={Intent.PRIMARY} icon={<Upload />} onClick={openDialog}>
        Upsert roles
      </Button>
      <Dialog
        className="role-upsert-dialog"
        isOpen={isOpen}
        onClose={closeDialog}
        title="Upsert roles"
        canOutsideClickClose={false}
      >
        <Form onSubmit={upsertUsers} initialValues={initialValues}>
          {({ handleSubmit, submitting, values }) => (
            <form onSubmit={handleSubmit}>
              <div className={Classes.DIALOG_BODY}>
                {error && (
                  <Callout className="role-upsert-dialog__error" intent={Intent.DANGER}>
                    {error}
                  </Callout>
                )}
                <Field component={FormTextArea} {...usernamesField} />
                <FormGroup label="Roles">
                  <Field name="roles">
                    {({ input }) => <RoleCheckboxes value={input.value} onChange={input.onChange} />}
                  </Field>
                </FormGroup>
              </div>
              <div className={Classes.DIALOG_FOOTER}>
                <div className={Classes.DIALOG_FOOTER_ACTIONS}>
                  <Button text="Cancel" disabled={submitting} onClick={closeDialog} />
                  <Button
                    type="submit"
                    text="Submit"
                    intent={Intent.PRIMARY}
                    loading={submitting}
                    disabled={!Object.values(values.roles).some(checked => checked)}
                  />
                </div>
              </div>
            </form>
          )}
        </Form>
      </Dialog>
    </>
  );
}
