import { Button } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { ProblemSetterRole } from '../../../../modules/api/problem';
import {
  problemSettersQueryOptions,
  setProblemSettersMutationOptions,
} from '../../../../modules/queries/problemSetter';
import ProblemSettersEditForm from '../ProblemSettersEditForm/ProblemSettersEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

const setterFields = [
  { role: ProblemSetterRole.Writer, title: 'Writers' },
  { role: ProblemSetterRole.Developer, title: 'Developers' },
  { role: ProblemSetterRole.Tester, title: 'Testers' },
  { role: ProblemSetterRole.Editorialist, title: 'Editorialists' },
];

function parseUsernames(usernames) {
  return (usernames || '')
    .split(',')
    .map(username => username.trim())
    .filter(username => username);
}

export default function ProblemSettersPanel({ problemJid }) {
  const {
    data: { data: setterJidsMap, profilesMap },
  } = useSuspenseQuery(problemSettersQueryOptions(problemJid));

  const setSettersMutation = useMutation(setProblemSettersMutationOptions(problemJid));

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '250px' };

  const getSetterUsernames = role => (setterJidsMap[role] || []).map(jid => profilesMap[jid]?.username).join(', ');

  const rows = setterFields.map(({ role, title }) => ({ key: role, title, value: getSetterUsernames(role) }));

  const setSetters = async data => {
    const setterUsernamesMap = Object.fromEntries(setterFields.map(({ role }) => [role, parseUsernames(data[role])]));

    await setSettersMutation.mutateAsync(setterUsernamesMap, {
      onSuccess: () => toastActions.showSuccessToast('Setters updated.'),
    });
    setIsEditing(false);
  };

  const renderEditButton = () => {
    return (
      !isEditing && (
        <Button small variant="outlined" icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = Object.fromEntries(setterFields.map(({ role }) => [role, getSetterUsernames(role)]));
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
    <div>
      <Flex asChild gap={2} alignItems="center">
        <h4>
          <span>Setters</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
