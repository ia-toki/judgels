import { Button, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useParams } from '@tanstack/react-router';
import { useState } from 'react';

import ContentWithTopbar from '../../../../components/ContentWithTopbar/ContentWithTopbar';
import { worldLanguageNamesMap } from '../../../../modules/api/worldLanguage';
import { problemBySlugQueryOptions } from '../../../../modules/queries/problem';
import {
  createProblemEditorialMutationOptions,
  problemEditorialExistsQueryOptions,
} from '../../../../modules/queries/problemEditorial';
import LanguageSelect from '../../catalog/LanguageSelect/LanguageSelect';

import * as toastActions from '../../../../modules/toast/toastActions';

// A topbar item's path becomes its element id, so it must not be `text`:
// that is the id of the editorial form's text area, which the rich text editor looks up by id.
const topbarItems = [
  { path: 'content', title: 'Content' },
  { path: 'languages', title: 'Languages' },
  { path: 'media', title: 'Media' },
];

export default function ProblemEditorialLayout() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const { data: editorialExists } = useSuspenseQuery(problemEditorialExistsQueryOptions(problemJid));

  const createEditorialMutation = useMutation(createProblemEditorialMutationOptions(problemJid));

  const [initialLanguage, setInitialLanguage] = useState('en-US');

  const createEditorial = () => {
    createEditorialMutation.mutate(
      { initialLanguage },
      {
        onSuccess: () => toastActions.showSuccessToast('Editorial created.'),
      }
    );
  };

  // A problem has no editorial until one is created, and only then is there anything for the tabs to show.
  if (!editorialExists) {
    return (
      <Flex flexDirection="column" gap={2}>
        <h4>Editorial</h4>
        <p>
          <small>This problem has no editorial.</small>
        </p>
        <Flex gap={2} alignItems="center">
          <LanguageSelect
            languages={Object.keys(worldLanguageNamesMap)}
            value={initialLanguage}
            label="Initial language"
            filterable
            onChange={setInitialLanguage}
          />
          <Button
            text="Create editorial"
            intent={Intent.PRIMARY}
            loading={createEditorialMutation.isPending}
            onClick={createEditorial}
          />
        </Flex>
      </Flex>
    );
  }

  return (
    <ContentWithTopbar items={topbarItems} basePath={`/admin/problems/${problemSlug}/editorial`} secondary>
      <Outlet />
    </ContentWithTopbar>
  );
}
