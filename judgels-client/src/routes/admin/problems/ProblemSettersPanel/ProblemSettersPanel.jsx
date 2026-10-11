import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { BadRequestError } from '../../../../modules/api/error';
import { ProblemErrors, ProblemSetterRole } from '../../../../modules/api/problem';
import { SubmissionError } from '../../../../modules/form/submissionError';
import {
  problemSettersQueryOptions,
  setProblemSettersMutationOptions,
} from '../../../../modules/queries/problemSetter';
import ProblemSettersEditForm from '../ProblemSettersEditForm/ProblemSettersEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

const setterFields = [
  { role: ProblemSetterRole.Writer, name: 'writerUsernames', title: 'Writers' },
  { role: ProblemSetterRole.Developer, name: 'developerUsernames', title: 'Developers' },
  { role: ProblemSetterRole.Tester, name: 'testerUsernames', title: 'Testers' },
  { role: ProblemSetterRole.Editorialist, name: 'editorialistUsernames', title: 'Editorialists' },
];

function parseUsernames(usernames) {
  return (usernames || '')
    .split(',')
    .map(username => username.trim())
    .filter(username => username);
}

export default function ProblemSettersPanel({ problemJid, canEdit }) {
  const {
    data: { data: setterJidsMap, profilesMap },
  } = useSuspenseQuery(problemSettersQueryOptions(problemJid));

  const setSettersMutation = useMutation(setProblemSettersMutationOptions(problemJid));

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '250px' };

  const getSetterUsernames = role => (setterJidsMap[role] || []).map(jid => profilesMap[jid]?.username).join(', ');

  const rows = setterFields.map(({ role, name, title }) => ({ key: name, title, value: getSetterUsernames(role) }));

  const setSetters = async data => {
    const setterUsernamesMap = Object.fromEntries(
      setterFields.map(({ role, name }) => [role, parseUsernames(data[name])])
    );

    try {
      await setSettersMutation.mutateAsync(setterUsernamesMap, {
        onSuccess: () => toastActions.showSuccessToast('Setters updated.'),
      });
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemErrors.SetterUsernamesNotFound) {
        const usernamesNotFound = parseUsernames(error.args.usernames);
        const errors = {};
        setterFields.forEach(({ role, name }) => {
          const usernames = setterUsernamesMap[role].filter(username => usernamesNotFound.includes(username));
          if (usernames.length > 0) {
            errors[name] = 'Users not found: ' + usernames.join(', ');
          }
        });
        throw new SubmissionError(errors);
      }
      throw error;
    }
    setIsEditing(false);
  };

  const renderEditButton = () => {
    return (
      canEdit &&
      !isEditing && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = Object.fromEntries(setterFields.map(({ role, name }) => [name, getSetterUsernames(role)]));
      return (
        <ProblemSettersEditForm
          initialValues={initialValues}
          setterFields={setterFields}
          onSubmit={setSetters}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    return <FormTable keyStyles={keyStyles} rows={rows} />;
  };

  return (
    <section aria-label="Setters">
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Setters</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </section>
  );
}
