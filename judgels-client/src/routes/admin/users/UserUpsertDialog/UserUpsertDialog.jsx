import { Button, Classes, Dialog, Intent, TextArea } from '@blueprintjs/core';
import { Upload } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { upsertUsersMutationOptions } from '../../../../modules/queries/user';

export function UserUpsertDialog() {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [csv, setCsv] = useState('');
  const [result, setResult] = useState(undefined);

  const upsertMutation = useMutation(upsertUsersMutationOptions());

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
    setCsv('');
    setResult(undefined);
  };

  const handleSubmit = async () => {
    const response = await upsertMutation.mutateAsync(csv);
    setResult(response);
  };

  const renderForm = () => (
    <>
      <div className={Classes.DIALOG_BODY}>
        <p>Users can be created or updated via CSV.</p>

        <p>
          <strong>Allowed CSV headers</strong>
        </p>
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

        <p>
          If <code>jid</code> is provided, it will be used as the primary key. For example, if a user with that JID
          already exists, the user will be updated. Otherwise, <code>username</code> will be used as the primary key.
        </p>

        <p>
          <strong>Example 1:</strong> basic user creation
        </p>
        <pre>{`username,password,email,name\nandi,andiandi,andi@judgels.com,Andi Smith\nbudi,budibudi,budi@judgels.com,Budi Doe`}</pre>

        <p>
          <strong>Example 2:</strong> creating users with fixed JIDs, or updating usernames based on JIDs
        </p>
        <pre>{`jid,username,password,email,name,country\nJIDUSER11111111111111111111,andi,andiandi,andi@judgels.com,Andi Smith,ID\nJIDUSER22222222222222222222,budi,budibudi,budi@judgels.com,Budi Doe,SG`}</pre>

        <p>
          <strong>Example 3:</strong> resetting user passwords
        </p>
        <pre>{`username,password\nandi,newandipass\nbudi,newbudipass`}</pre>

        <hr />

        <p>CSV rows, including header:</p>
        <TextArea fill rows={10} value={csv} onChange={e => setCsv(e.target.value)} />
      </div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={toggleDialog} />
          <Button
            text="Submit"
            intent={Intent.PRIMARY}
            onClick={handleSubmit}
            disabled={!csv.trim()}
            loading={upsertMutation.isPending}
          />
        </div>
      </div>
    </>
  );

  const renderResult = () => (
    <>
      <div className={Classes.DIALOG_BODY}>
        <p>
          {result.createdUsernames.length} users created, {result.updatedUsernames.length} users updated.
        </p>
        {result.createdUsernames.length > 0 && (
          <div>
            <h5>Created ({result.createdUsernames.length})</h5>
            <pre>{result.createdUsernames.join('\n')}</pre>
          </div>
        )}
        {result.updatedUsernames.length > 0 && (
          <div>
            <h5>Updated ({result.updatedUsernames.length})</h5>
            <pre>{result.updatedUsernames.join('\n')}</pre>
          </div>
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
    <div className="content-card__section">
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
    </div>
  );
}
