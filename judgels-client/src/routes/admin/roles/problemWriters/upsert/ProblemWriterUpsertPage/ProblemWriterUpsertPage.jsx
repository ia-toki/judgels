import { Button, Callout, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../../../components/ActionButtons/ActionButtons';
import { BreadcrumbTitle } from '../../../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { FormTextArea } from '../../../../../../components/forms/FormTextArea/FormTextArea';
import { FormUnsavedChangesAlert } from '../../../../../../components/forms/UnsavedChangesAlert/UnsavedChangesAlert';
import { parseUsernames } from '../../../../../../components/forms/UsernamesEditForm/UsernamesEditForm';
import { Required } from '../../../../../../components/forms/validations';
import { UserRole, isSuperadmin } from '../../../../../../modules/api/userRole';
import { upsertUserRolesMutationOptions, userRolesQueryOptions } from '../../../../../../modules/queries/userRole';

import * as toastActions from '../../../../../../modules/toast/toastActions';

const MAX_UPSERTED_WRITERS = 100;

function validateUsernames(text) {
  return (
    Required(text) ||
    (parseUsernames(text).length > MAX_UPSERTED_WRITERS ? `At most ${MAX_UPSERTED_WRITERS} users at once` : undefined)
  );
}

const usernamesField = {
  name: 'usernames',
  label: 'Usernames',
  labelHelper: '(one username per line)',
  rows: 6,
  isCode: true,
  validate: validateUsernames,
  autoFocus: true,
};

export default function ProblemWriterUpsertPage() {
  const navigate = useNavigate();
  const [error, setError] = useState(undefined);

  const {
    data: { data: users, profilesMap },
  } = useSuspenseQuery(userRolesQueryOptions());
  const superadminJid = users.find(({ roles }) => isSuperadmin(roles))?.userJid;
  const superadminUsername = profilesMap[superadminJid]?.username;

  const upsertRolesMutation = useMutation(upsertUserRolesMutationOptions());

  const goToWriters = () => navigate({ to: '/admin/roles/problem-writers', ignoreBlocker: true });

  const upsertWriters = async (values, form) => {
    const usernames = parseUsernames(values.usernames);
    if (usernames.includes(superadminUsername)) {
      setError(`${superadminUsername} already holds every role.`);
      return;
    }

    const { upsertedUserProfilesMap } = await upsertRolesMutation.mutateAsync({
      usernames,
      roles: [UserRole.ProblemWriter],
    });

    const notFound = usernames.filter(username => !upsertedUserProfilesMap[username]);
    if (notFound.length < usernames.length) {
      toastActions.showSuccessToast('Writers added.');
    }
    if (notFound.length === 0) {
      goToWriters();
    } else {
      form.change('usernames', notFound.join('\n'));
      setError(`User not found: ${notFound.join(', ')}.`);
    }
  };

  return (
    <ContentCard
      title={
        <BreadcrumbTitle
          items={[
            { text: 'Roles', to: '/admin/roles' },
            { text: 'Problem Writers', to: '/admin/roles/problem-writers' },
            { text: 'Upsert' },
          ]}
        />
      }
    >
      <Flex flexDirection="column" gap={4}>
        {error && <Callout intent={Intent.DANGER}>{error}</Callout>}
        <Form onSubmit={upsertWriters}>
          {({ handleSubmit, submitting }) => (
            <form onSubmit={handleSubmit}>
              <FormUnsavedChangesAlert />
              <Field component={FormTextArea} {...usernamesField} />
              <ActionButtons justifyContent="end">
                <Button text="Cancel" disabled={submitting} onClick={goToWriters} />
                <Button type="submit" text="Submit" intent={Intent.PRIMARY} loading={submitting} />
              </ActionButtons>
            </form>
          )}
        </Form>
      </Flex>
    </ContentCard>
  );
}
