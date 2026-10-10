import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { problemQueryOptions } from '../../../../modules/queries/problem';
import {
  addProblemEditorialLanguageMutationOptions,
  disableProblemEditorialLanguageMutationOptions,
  enableProblemEditorialLanguageMutationOptions,
  makeProblemEditorialLanguageDefaultMutationOptions,
  problemEditorialLanguagesQueryOptions,
} from '../../../../modules/queries/problemEditorial';
import ProblemLanguagesPanel from '../ProblemLanguagesPanel/ProblemLanguagesPanel';

export default function ProblemEditorialLanguagesPage() {
  const { problemJid } = useParams({ strict: false });

  const {
    data: { config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { enabledLanguages, disabledLanguages, defaultLanguage },
  } = useSuspenseQuery(problemEditorialLanguagesQueryOptions(problemJid));

  const addLanguageMutation = useMutation(addProblemEditorialLanguageMutationOptions(problemJid));
  const enableLanguageMutation = useMutation(enableProblemEditorialLanguageMutationOptions(problemJid));
  const disableLanguageMutation = useMutation(disableProblemEditorialLanguageMutationOptions(problemJid));
  const makeLanguageDefaultMutation = useMutation(makeProblemEditorialLanguageDefaultMutationOptions(problemJid));

  return (
    <ProblemLanguagesPanel
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
