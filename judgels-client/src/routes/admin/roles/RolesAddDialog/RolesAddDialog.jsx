import { Button, Classes, Dialog, FormGroup, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { Required } from '../../../../components/forms/validations';
import { RoleSelect, emptyRoles, getRoleFields } from '../RoleSelect/RoleSelect';

import './RolesAddDialog.scss';

const usernamesField = {
  name: 'usernames',
  label: 'Usernames',
  labelHelper: '(one username per line)',
  rows: 8,
  isCode: true,
  validate: Required,
  autoFocus: true,
};

function parseUsernames(text) {
  return text
    .split('\n')
    .map(s => s.trim())
    .filter(s => s.length > 0);
}

export function RolesAddDialog({ onAdd }) {
  const [isOpen, setIsOpen] = useState(false);

  const roleFields = getRoleFields();

  const openDialog = () => {
    setIsOpen(true);
  };

  const closeDialog = () => {
    setIsOpen(false);
  };

  const handleAdd = values => {
    const names = parseUsernames(values.usernames);
    const roles = {};
    for (const { key } of roleFields) {
      roles[key] = values[key] || '';
    }
    onAdd(names.map(username => ({ username, ...roles })));
    setIsOpen(false);
  };

  return (
    <>
      <Button icon={<Plus />} onClick={openDialog} disabled={isOpen}>
        Add users
      </Button>
      <Dialog
        className="roles-add-dialog"
        isOpen={isOpen}
        onClose={closeDialog}
        title="Add users"
        canOutsideClickClose={false}
      >
        <Form onSubmit={handleAdd} initialValues={emptyRoles()}>
          {({ handleSubmit }) => (
            <form onSubmit={handleSubmit}>
              <div className={Classes.DIALOG_BODY}>
                <Field component={FormTextArea} {...usernamesField} />
                <FormGroup label="Roles">
                  <Flex gap={2}>
                    {roleFields.map(({ key, label }) => (
                      <FormGroup key={key} label={label}>
                        <Field name={key}>
                          {({ input }) => (
                            <RoleSelect label={`new ${key} role`} value={input.value} onChange={input.onChange} />
                          )}
                        </Field>
                      </FormGroup>
                    ))}
                  </Flex>
                </FormGroup>
              </div>
              <div className={Classes.DIALOG_FOOTER}>
                <div className={Classes.DIALOG_FOOTER_ACTIONS}>
                  <Button text="Cancel" onClick={closeDialog} />
                  <Button type="submit" text="Add" intent={Intent.PRIMARY} />
                </div>
              </div>
            </form>
          )}
        </Form>
      </Dialog>
    </>
  );
}
