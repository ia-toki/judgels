import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { lessonQueryOptions } from '../../../../modules/queries/lesson';
import {
  addLessonStatementLanguageMutationOptions,
  disableLessonStatementLanguageMutationOptions,
  enableLessonStatementLanguageMutationOptions,
  lessonStatementLanguagesQueryOptions,
  makeLessonStatementLanguageDefaultMutationOptions,
} from '../../../../modules/queries/lessonStatement';
import LanguagesPanel from '../../catalog/LanguagesPanel/LanguagesPanel';

export default function LessonStatementLanguagesPage() {
  const { lessonJid } = useParams({ strict: false });

  const {
    data: { config },
  } = useSuspenseQuery(lessonQueryOptions(lessonJid));

  const {
    data: { enabledLanguages, disabledLanguages, defaultLanguage },
  } = useSuspenseQuery(lessonStatementLanguagesQueryOptions(lessonJid));

  const addLanguageMutation = useMutation(addLessonStatementLanguageMutationOptions(lessonJid));
  const enableLanguageMutation = useMutation(enableLessonStatementLanguageMutationOptions(lessonJid));
  const disableLanguageMutation = useMutation(disableLessonStatementLanguageMutationOptions(lessonJid));
  const makeLanguageDefaultMutation = useMutation(makeLessonStatementLanguageDefaultMutationOptions(lessonJid));

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
