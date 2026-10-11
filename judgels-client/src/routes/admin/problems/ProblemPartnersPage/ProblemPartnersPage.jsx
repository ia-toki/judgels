import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { UserRef } from '../../../../components/UserRef/UserRef';
import { partnerPermissionNamesMap } from '../../../../modules/api/problemPartner';
import { problemBySlugQueryOptions } from '../../../../modules/queries/problem';
import {
  problemPartnersQueryOptions,
  setProblemPartnersMutationOptions,
} from '../../../../modules/queries/problemPartner';
import ProblemPartnersEditForm from '../ProblemPartnersEditForm/ProblemPartnersEditForm';
import { parseCsv, toCsv } from './partnersCsv';

import * as toastActions from '../../../../modules/toast/toastActions';

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
    await setPartnersMutation.mutateAsync(parseCsv(data.csv).partners, {
      onSuccess: () => toastActions.showSuccessToast('Partners updated.'),
    });
    setIsEditing(false);
  };

  const renderEditButton = () => {
    return (
      !isEditing && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      return (
        <ProblemPartnersEditForm
          initialValues={{ csv: toCsv(partners, profilesMap) }}
          validateCsv={csv => parseCsv(csv).error}
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
            <th>Permission</th>
          </tr>
        </thead>
        <tbody>
          {partners.map(partner => (
            <tr key={partner.userJid}>
              <td>
                <UserRef profile={profilesMap[partner.userJid]} />
              </td>
              <td>{partnerPermissionNamesMap[partner.permission]}</td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
    );
  };

  return (
    <section aria-label="Partners">
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Partners</span>
          {renderEditButton()}
        </h4>
      </Flex>
      <p>Partners can view this problem and, when permitted, update it. Only its author and admins manage them.</p>
      {renderContent()}
    </section>
  );
}
