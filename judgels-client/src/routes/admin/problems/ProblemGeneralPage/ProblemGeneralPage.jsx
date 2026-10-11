import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { UserRef } from '../../../../components/UserRef/UserRef';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { problemTypeNamesMap } from '../../../../modules/api/problem';
import {
  problemBySlugQueryOptions,
  problemPermissionsQueryOptions,
  problemQueryOptions,
  updateProblemMutationOptions,
} from '../../../../modules/queries/problem';
import { basicProfileQueryOptions } from '../../../../modules/queries/profile';
import ProblemGeneralEditForm from '../ProblemGeneralEditForm/ProblemGeneralEditForm';
import ProblemSettersPanel from '../ProblemSettersPanel/ProblemSettersPanel';
import ProblemTopicTagsPanel from '../ProblemTopicTagsPanel/ProblemTopicTagsPanel';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemGeneralPage() {
  const navigate = useNavigate();
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const { data: problem } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { canEdit },
  } = useSuspenseQuery(problemPermissionsQueryOptions(problemJid));

  const { data: authorProfile } = useSuspenseQuery(basicProfileQueryOptions(problem.authorJid));

  const updateProblemMutation = useMutation(updateProblemMutationOptions(problemJid, problemSlug));

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '250px' };

  const rows = [
    { key: 'jid', title: 'JID', value: problem.jid },
    { key: 'slug', title: 'Slug', value: problem.slug },
    { key: 'type', title: 'Type', value: problemTypeNamesMap[problem.type] },
    { key: 'author', title: 'Author', value: <UserRef profile={authorProfile} /> },
    { key: 'additionalNote', title: 'Additional note', value: problem.additionalNote },
  ];

  const updateProblem = async data => {
    await updateProblemMutation.mutateAsync(
      { slug: data.slug, additionalNote: data.additionalNote || '' },
      {
        onSuccess: () => toastActions.showSuccessToast('Problem updated.'),
      }
    );
    setIsEditing(false);
    if (data.slug !== problemSlug) {
      navigate({ to: `/admin/problems/${data.slug}` });
    }
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
      const initialValues = {
        slug: problem.slug,
        additionalNote: problem.additionalNote,
      };
      return (
        <ProblemGeneralEditForm
          initialValues={initialValues}
          onSubmit={updateProblem}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    return <FormTable keyStyles={keyStyles} rows={rows} />;
  };

  return (
    <Flex flexDirection="column" gap={4}>
      <section aria-label="General">
        <Flex asChild justifyContent="space-between" alignItems="baseline">
          <h4>
            <span>General</span>
            {renderEditButton()}
          </h4>
        </Flex>
        {renderContent()}
      </section>
      <ProblemSettersPanel problemJid={problemJid} canEdit={canEdit} />
      <ProblemTopicTagsPanel problemJid={problemJid} canEdit={canEdit} />
    </Flex>
  );
}
