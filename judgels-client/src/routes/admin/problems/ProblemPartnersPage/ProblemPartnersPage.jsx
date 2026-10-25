import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { problemBySlugQueryOptions } from '../../../../modules/queries/problem';
import {
  problemPartnersQueryOptions,
  setProblemPartnersMutationOptions,
} from '../../../../modules/queries/problemPartner';
import ProblemPartnersEditForm from '../ProblemPartnersEditForm/ProblemPartnersEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

const MAX_PARTNERS = 100;

function parseUsernames(text) {
  const usernames = (text || '')
    .split('\n')
    .map(s => s.trim())
    .filter(s => s.length > 0);
  return [...new Set(usernames)];
}

function validateUsernames(text) {
  if (parseUsernames(text).length > MAX_PARTNERS) {
    return `At most ${MAX_PARTNERS} partners are allowed`;
  }
  return undefined;
}

export default function ProblemPartnersPage() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const {
    data: { data: partners, profilesMap },
  } = useSuspenseQuery(problemPartnersQueryOptions(problemJid));

  const setPartnersMutation = useMutation(setProblemPartnersMutationOptions(problemJid));

  const [isEditing, setIsEditing] = useState(false);

  const setPartners = async data => {
    const usernames = parseUsernames(data.usernames);
    await setPartnersMutation.mutateAsync(
      usernames.map(username => ({ username })),
      {
        onSuccess: () => toastActions.showSuccessToast('Partners updated.'),
      }
    );
    setIsEditing(false);
  };

  const renderEditButton = () => {
    return (
      !isEditing && (
        <ActionButtons>
          <Button intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
            Edit
          </Button>
        </ActionButtons>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = {
        usernames: partners.map(partner => profilesMap[partner.userJid]?.username).join('\n'),
      };
      return (
        <ProblemPartnersEditForm
          initialValues={initialValues}
          validateUsernames={validateUsernames}
          onSubmit={setPartners}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    if (partners.length === 0) {
      return (
        <p>
          <small>No partners.</small>
        </p>
      );
    }
    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th>User</th>
          </tr>
        </thead>
        <tbody>
          {partners.map(partner => (
            <tr key={partner.userJid}>
              <td>
                <UserRef profile={profilesMap[partner.userJid]} />
              </td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
    );
  };

  return (
    <Flex flexDirection="column" gap={2}>
      <Flex flexDirection="column">
        <h4>Partners</h4>
        <p>
          <small>Users that can also edit this problem.</small>
        </p>
      </Flex>
      {renderEditButton()}
      {renderContent()}
    </Flex>
  );
}
