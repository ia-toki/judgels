import { Button, HTMLSelect, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useParams } from '@tanstack/react-router';
import { useState } from 'react';

import ContentWithTopbar from '../../../../components/ContentWithTopbar/ContentWithTopbar';
import { worldLanguageNamesMap } from '../../../../modules/api/worldLanguage';
import { problemQueryOptions } from '../../../../modules/queries/problem';
import { createProblemEditorialMutationOptions } from '../../../../modules/queries/problemEditorial';

import * as toastActions from '../../../../modules/toast/toastActions';

// A topbar item's path becomes its element id, so it must not be `text`:
// that is the id of the editorial form's text area, which the rich text editor looks up by id.
const topbarItems = [
  { path: 'content', title: 'Content' },
  { path: 'languages', title: 'Languages' },
  { path: 'media', title: 'Media' },
];

export default function ProblemEditorialLayout() {
  const { problemJid } = useParams({ strict: false });

  const {
    data: { hasEditorial, config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

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
  if (!hasEditorial) {
    return (
      <Flex flexDirection="column" gap={2}>
        <h4>Editorial</h4>
        <p>
          <small>This problem has no editorial.</small>
        </p>
        {config.canEdit && (
          <Flex gap={2} alignItems="center">
            <HTMLSelect
              aria-label="Initial language"
              value={initialLanguage}
              onChange={e => setInitialLanguage(e.target.value)}
            >
              {Object.keys(worldLanguageNamesMap).map(language => (
                <option key={language} value={language}>
                  {worldLanguageNamesMap[language]}
                </option>
              ))}
            </HTMLSelect>
            <Button
              text="Create editorial"
              intent={Intent.PRIMARY}
              loading={createEditorialMutation.isPending}
              onClick={createEditorial}
            />
          </Flex>
        )}
      </Flex>
    );
  }

  return (
    <ContentWithTopbar items={topbarItems} basePath={`/admin/problems/${problemJid}/editorial`}>
      <Outlet />
    </ContentWithTopbar>
  );
}
