import { Button, Collapse, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { Required } from '../../../../components/forms/validations';
import { upsertUsersMutationOptions } from '../../../../modules/queries/user';

export default function UserUpsertPage() {
  const navigate = useNavigate();
  const [result, setResult] = useState(undefined);
  const [isInstructionOpen, setIsInstructionOpen] = useState(false);

  const upsertMutation = useMutation(upsertUsersMutationOptions());

  const goToUsers = () => navigate({ to: '/admin/users' });

  const handleSubmit = async values => {
    const response = await upsertMutation.mutateAsync(values.csv);
    setResult(response);
    window.scrollTo(0, 0);
  };

  const csvField = {
    name: 'csv',
    label: 'CSV rows, including header',
    rows: 10,
    isCode: true,
    validate: Required,
    autoFocus: true,
  };

  const renderInstructions = () => (
    <Flex flexDirection="column" alignItems="start" gap={2}>
      <Button
        onClick={() => setIsInstructionOpen(!isInstructionOpen)}
        rightIcon={isInstructionOpen ? 'chevron-up' : 'chevron-down'}
        text="Instructions"
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
          If <code>jid</code> is provided, it will be used as the primary key. For example, if a user with that JID
          already exists, the user will be updated. Otherwise, <code>username</code> will be used as the primary key.
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
    </Flex>
  );

  const renderForm = () => (
    <Form onSubmit={handleSubmit}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <Field component={FormTextArea} {...csvField} />
          <ActionButtons justifyContent="end">
            <Button text="Cancel" disabled={submitting} onClick={goToUsers} />
            <Button type="submit" text="Submit" intent={Intent.PRIMARY} loading={submitting} />
          </ActionButtons>
        </form>
      )}
    </Form>
  );

  const renderResult = () => (
    <Flex flexDirection="column" gap={2}>
      <div>
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
      <ActionButtons justifyContent="end">
        <Button text="Done" intent={Intent.PRIMARY} onClick={goToUsers} />
      </ActionButtons>
    </Flex>
  );

  const renderContent = () => {
    if (result) {
      return renderResult();
    }
    return (
      <Flex flexDirection="column" gap={2}>
        {renderInstructions()}
        {renderForm()}
      </Flex>
    );
  };

  return <ContentCard title={<BreadcrumbTitle parent="Users" current="Upsert" />}>{renderContent()}</ContentCard>;
}
