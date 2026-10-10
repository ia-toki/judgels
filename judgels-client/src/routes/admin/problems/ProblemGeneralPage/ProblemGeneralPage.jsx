import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { UserRef } from '../../../../components/UserRef/UserRef';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { BadRequestError } from '../../../../modules/api/error';
import { ProblemErrors, ProblemSetterRole, problemTypeNamesMap } from '../../../../modules/api/problem';
import { SubmissionError } from '../../../../modules/form/submissionError';
import { problemQueryOptions, updateProblemMutationOptions } from '../../../../modules/queries/problem';
import ProblemGeneralEditForm from '../ProblemGeneralEditForm/ProblemGeneralEditForm';

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

export default function ProblemGeneralPage() {
  const { problemJid } = useParams({ strict: false });

  const {
    data: { data: problem, setterJidsMap, topicTags, config, profilesMap },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const updateProblemMutation = useMutation(updateProblemMutationOptions(problemJid));

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '250px' };

  const getSetterUsernames = role => (setterJidsMap[role] || []).map(jid => profilesMap[jid]?.username).join(', ');

  const rows = [
    { key: 'jid', title: 'JID', value: problem.jid },
    { key: 'slug', title: 'Slug', value: problem.slug },
    { key: 'type', title: 'Type', value: problemTypeNamesMap[problem.type] },
    { key: 'author', title: 'Author', value: <UserRef profile={profilesMap[problem.authorJid]} /> },
    { key: 'additionalNote', title: 'Additional note', value: problem.additionalNote },
    ...setterFields.map(({ role, name, title }) => ({ key: name, title, value: getSetterUsernames(role) })),
    {
      key: 'topicTags',
      title: 'Tags',
      value: topicTags
        .slice()
        .sort()
        .map(tag => tag.substring('topic-'.length))
        .join(', '),
    },
  ];

  const updateProblem = async data => {
    const setterUsernamesMap = Object.fromEntries(
      setterFields.map(({ role, name }) => [role, parseUsernames(data[name])])
    );

    try {
      await updateProblemMutation.mutateAsync(
        {
          slug: data.slug,
          additionalNote: data.additionalNote || '',
          setterUsernamesMap,
          topicTags: data.topicTags || [],
        },
        {
          onSuccess: () => toastActions.showSuccessToast('Problem updated.'),
        }
      );
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
      config.canEdit &&
      !isEditing && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = {
        slug: problem.slug,
        additionalNote: problem.additionalNote,
        ...Object.fromEntries(setterFields.map(({ role, name }) => [name, getSetterUsernames(role)])),
        topicTags,
      };
      return (
        <ProblemGeneralEditForm
          initialValues={initialValues}
          setterFields={setterFields}
          onSubmit={updateProblem}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    return <FormTable keyStyles={keyStyles} rows={rows} />;
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>General</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
