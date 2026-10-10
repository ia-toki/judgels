import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { problemBySlugQueryOptions, problemQueryOptions } from '../../../../modules/queries/problem';
import {
  addProblemStatementLanguageMutationOptions,
  disableProblemStatementLanguageMutationOptions,
  enableProblemStatementLanguageMutationOptions,
  makeProblemStatementLanguageDefaultMutationOptions,
  problemStatementLanguagesQueryOptions,
} from '../../../../modules/queries/problemStatement';
import LanguagesPanel from '../../catalog/LanguagesPanel/LanguagesPanel';

export default function ProblemStatementLanguagesPage() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const {
    data: { config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { enabledLanguages, disabledLanguages, defaultLanguage },
  } = useSuspenseQuery(problemStatementLanguagesQueryOptions(problemJid));

  const addLanguageMutation = useMutation(addProblemStatementLanguageMutationOptions(problemJid));
  const enableLanguageMutation = useMutation(enableProblemStatementLanguageMutationOptions(problemJid));
  const disableLanguageMutation = useMutation(disableProblemStatementLanguageMutationOptions(problemJid));
  const makeLanguageDefaultMutation = useMutation(makeProblemStatementLanguageDefaultMutationOptions(problemJid));

  return (
    <LanguagesPanel
      enabledLanguages={enabledLanguages}
      disabledLanguages={disabledLanguages}
      defaultLanguage={defaultLanguage}
      canEdit={config.canEdit}
      onAddLanguage={addLanguageMutation.mutateAsync}
      onEnableLanguage={enableLanguageMutation.mutateAsync}
      onDisableLanguage={disableLanguageMutation.mutateAsync}
      onMakeLanguageDefault={makeLanguageDefaultMutation.mutateAsync}
    />
  );
}
