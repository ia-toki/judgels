import { Button, Classes, Collapse, Dialog, Intent } from '@blueprintjs/core';
import { Upload } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { Required } from '../../../../components/forms/validations';
import { upsertUsersMutationOptions } from '../../../../modules/queries/user';

export function UserUpsertDialog() {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [result, setResult] = useState(undefined);
  const [isInstructionOpen, setIsInstructionOpen] = useState(false);

  const upsertMutation = useMutation(upsertUsersMutationOptions());

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
    setResult(undefined);
  };

  const handleSubmit = async values => {
    const response = await upsertMutation.mutateAsync(values.csv);
    setResult(response);
  };

  const csvField = {
    name: 'csv',
    label: 'CSV rows, including header',
    rows: 10,
    isCode: true,
    validate: Required,
  };

  const renderForm = () => (
    <Form onSubmit={handleSubmit}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <div className={Classes.DIALOG_BODY}>
            <Button
              onClick={() => setIsInstructionOpen(!isInstructionOpen)}
              rightIcon={isInstructionOpen ? 'chevron-up' : 'chevron-down'}
              text="Instructions"
              style={{ marginBottom: '10px' }}
            />
            <Collapse isOpen={isInstructionOpen}>
              <h5>Allowed CSV headers</h5>
              <ul>
                <li>
                  <code>jid</code>
                </li>
                <li>
                  <code>username</code> (mandatory only when creating users)
                </li>
                <li>
                  <code>password</code> (plaintext, mandatory only when creating users)
                </li>
                <li>
                  <code>email</code> (mandatory only when creating users)
                </li>
                <li>
                  <code>name</code>
                </li>
                <li>
                  <code>country</code> (two-letter country code)
                </li>
              </ul>
              <br />
              <p>
                If <code>jid</code> is provided, it will be used as the primary key. For example, if a user with that
                JID already exists, the user will be updated. Otherwise, <code>username</code> will be used as the
                primary key.
              </p>
              <hr />
              <h5>Example 1: basic user creation</h5>
              <pre>{`username,password,email,name\nandi,andiandi,andi@judgels.com,Andi Smith\nbudi,budibudi,budi@judgels.com,Budi Doe`}</pre>
              <hr />
              <h5>Example 2: creating users with fixed JIDs, or updating usernames based on JIDs</h5>
              <pre>{`jid,username,password,email,name,country\nJIDUSER11111111111111111111,andi,andiandi,andi@judgels.com,Andi Smith,ID\nJIDUSER22222222222222222222,budi,budibudi,budi@judgels.com,Budi Doe,SG`}</pre>
              <hr />
              <h5>Example 3: resetting user passwords</h5>
              <pre>{`username,password\nandi,newandipass\nbudi,newbudipass`}</pre>
              <hr />
            </Collapse>

            <Field component={FormTextArea} {...csvField} />
          </div>
          <div className={Classes.DIALOG_FOOTER}>
            <div className={Classes.DIALOG_FOOTER_ACTIONS}>
              <Button text="Cancel" onClick={toggleDialog} />
              <Button type="submit" text="Submit" intent={Intent.PRIMARY} loading={submitting} />
            </div>
          </div>
        </form>
      )}
    </Form>
  );

  const renderResult = () => (
    <>
      <div className={Classes.DIALOG_BODY}>
        <p>
          {result.createdUsernames.length} users created, {result.updatedUsernames.length} users updated.
        </p>
        {result.createdUsernames.length > 0 && (
          <>
            <hr />
            <h5>Created ({result.createdUsernames.length})</h5>
            <pre>{result.createdUsernames.join('\n')}</pre>
          </>
        )}
        {result.updatedUsernames.length > 0 && (
          <>
            <hr />
            <h5>Updated ({result.updatedUsernames.length})</h5>
            <pre>{result.updatedUsernames.join('\n')}</pre>
          </>
        )}
      </div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Done" intent={Intent.PRIMARY} onClick={toggleDialog} />
        </div>
      </div>
    </>
  );

  return (
    <>
      <Button intent={Intent.PRIMARY} icon={<Upload />} onClick={toggleDialog} disabled={isDialogOpen}>
        Upsert users
      </Button>
      <Dialog
        isOpen={isDialogOpen}
        onClose={toggleDialog}
        title={result ? 'Upsert results' : 'Upsert users'}
        canOutsideClickClose={false}
        style={{ width: 700 }}
      >
        {result ? renderResult() : renderForm()}
      </Dialog>
    </>
  );
}
