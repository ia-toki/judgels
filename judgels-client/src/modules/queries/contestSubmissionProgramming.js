import { getGradingLanguageEditorSubmissionFilename } from '../api/gabriel/language';
import { contestSubmissionProgrammingAPI } from '../api/uriel/contestSubmissionProgramming';
import { getToken } from '../session';

export const createProgrammingSubmissionMutationOptions = (contestJid, problemJid) => ({
  mutationFn: async data => {
    let sources = {};
    Object.keys(data.sourceTexts ?? []).forEach(key => {
      sources['sourceFiles.' + key] = new File(
        [data.sourceTexts[key]],
        getGradingLanguageEditorSubmissionFilename(data.gradingLanguage),
        { type: 'text/plain' }
      );
    });
    Object.keys(data.sourceFiles ?? []).forEach(key => {
      sources['sourceFiles.' + key] = data.sourceFiles[key];
    });

    await contestSubmissionProgrammingAPI.createSubmission(
      getToken(),
      contestJid,
      problemJid,
      data.gradingLanguage,
      sources
    );
  },
});
