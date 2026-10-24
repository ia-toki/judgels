import { Button, Callout, FormGroup, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { Required } from '../../../../components/forms/validations';
import { upsertUserRolesMutationOptions, userRolesQueryOptions } from '../../../../modules/queries/userRole';
import { RoleCheckboxes } from '../RoleCheckboxes/RoleCheckboxes';

import * as toastActions from '../../../../modules/toast/toastActions';

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

export default function RoleUpsertPage() {
  const navigate = useNavigate();
  const [error, setError] = useState(undefined);

  const {
    data: { data: roles, profilesMap },
  } = useSuspenseQuery(userRolesQueryOptions());
  const superadminJid = roles.find(({ role }) => role.account === 'SUPERADMIN')?.userJid;
  const superadminUsername = profilesMap[superadminJid]?.username;

  const upsertRolesMutation = useMutation(upsertUserRolesMutationOptions());

  const goToRoles = () => navigate({ to: '/admin/roles' });

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
      goToRoles();
    } else {
      // Leave only the usernames that still need fixing.
      form.change('usernames', notFound.join('\n'));
      setError(`User not found: ${notFound.join(', ')}.`);
    }
  };

  return (
    <ContentCard title={<BreadcrumbTitle parent="Roles" current="Upsert" />}>
      <Flex flexDirection="column" gap={4}>
        {error && <Callout intent={Intent.DANGER}>{error}</Callout>}
        <Form onSubmit={upsertUsers} initialValues={initialValues}>
          {({ handleSubmit, submitting, values }) => (
            <form onSubmit={handleSubmit}>
              <Field component={FormTextArea} {...usernamesField} />
              <FormGroup label="Roles">
                <Field name="roles">
                  {({ input }) => <RoleCheckboxes value={input.value} onChange={input.onChange} />}
                </Field>
              </FormGroup>
              <ActionButtons justifyContent="end">
                <Button text="Cancel" disabled={submitting} onClick={goToRoles} />
                <Button
                  type="submit"
                  text="Submit"
                  intent={Intent.PRIMARY}
                  loading={submitting}
                  disabled={!Object.values(values.roles).some(checked => checked)}
                />
              </ActionButtons>
            </form>
          )}
        </Form>
      </Flex>
    </ContentCard>
  );
}
