import { Button, Checkbox, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { allLanguagesAllowed, getGradingLanguageName, gradingLanguages } from '../../../../modules/api/gradingLanguage';
import { problemBySlugQueryOptions, problemQueryOptions } from '../../../../modules/queries/problem';
import {
  problemGradingLanguageRestrictionQueryOptions,
  updateProblemGradingLanguageRestrictionMutationOptions,
} from '../../../../modules/queries/problemGrading';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemGradingLanguageRestrictionPage() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const {
    data: { config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const { data: languageRestriction } = useSuspenseQuery(problemGradingLanguageRestrictionQueryOptions(problemJid));

  const updateLanguageRestrictionMutation = useMutation(
    updateProblemGradingLanguageRestrictionMutationOptions(problemJid)
  );

  const [isAllowedAll, setIsAllowedAll] = useState(allLanguagesAllowed(languageRestriction));
  const [allowedLanguages, setAllowedLanguages] = useState(languageRestriction.allowedLanguageNames);

  const toggleLanguage = language => {
    setAllowedLanguages(
      allowedLanguages.includes(language)
        ? allowedLanguages.filter(l => l !== language)
        : [...allowedLanguages, language]
    );
  };

  const updateLanguageRestriction = () => {
    // No allowed languages means no restriction, so restricting to nothing cannot be expressed.
    const allowedLanguageNames = isAllowedAll ? [] : gradingLanguages.filter(l => allowedLanguages.includes(l));
    updateLanguageRestrictionMutation.mutate(
      { allowedLanguageNames },
      {
        onSuccess: () => toastActions.showSuccessToast('Allowed languages updated.'),
      }
    );
  };

  return (
    <Flex flexDirection="column" gap={2}>
      <h4>Languages</h4>
      <div>
        <Checkbox
          label="Allow all"
          checked={isAllowedAll}
          disabled={!config.canEdit}
          onChange={e => setIsAllowedAll(e.target.checked)}
        />
        {gradingLanguages.map(language => (
          <Checkbox
            key={language}
            label={getGradingLanguageName(language)}
            checked={isAllowedAll || allowedLanguages.includes(language)}
            disabled={!config.canEdit || isAllowedAll}
            onChange={() => toggleLanguage(language)}
          />
        ))}
      </div>
      {config.canEdit && (
        <ActionButtons>
          <Button
            text="Save"
            intent={Intent.PRIMARY}
            disabled={!isAllowedAll && allowedLanguages.length === 0}
            loading={updateLanguageRestrictionMutation.isPending}
            onClick={updateLanguageRestriction}
          />
        </ActionButtons>
      )}
    </Flex>
  );
}
