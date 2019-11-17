import { push } from 'connected-react-router';

import { ProblemSubmissionFormData } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionForm/ProblemSubmissionForm';
import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { NotFoundError } from '../../../../../../../../modules/api/error';

export const chapterSubmissionActions = {
  getSubmissions: (chapterJid: string, userJid?: string, problemJid?: string, page?: number) => {
    return async (dispatch, getState, { chapterSubmissionProgrammingAPI }) => {
      const token = selectToken(getState());
      return await chapterSubmissionProgrammingAPI.getSubmissions(token, chapterJid, userJid, problemJid, page);
    };
  },

  getSubmissionWithSource: (chapterJid: string, submissionId: number, language?: string) => {
    return async (dispatch, getState, { chapterSubmissionProgrammingAPI }) => {
      const token = selectToken(getState());
      const submissionWithSource = await chapterSubmissionProgrammingAPI.getSubmissionWithSource(
        token,
        submissionId,
        language
      );
      if (chapterJid !== submissionWithSource.data.submission.containerJid) {
        throw new NotFoundError();
      }
      return submissionWithSource;
    };
  },

  createSubmission: (
    courseSlug: string,
    chapterJid: string,
    chapterAlias: string,
    problemJid: string,
    data: ProblemSubmissionFormData
  ) => {
    return async (dispatch, getState, { chapterSubmissionProgrammingAPI, toastActions }) => {
      const token = selectToken(getState());
      let sourceFiles = {};
      Object.keys(data.sourceFiles).forEach(key => {
        sourceFiles['sourceFiles.' + key] = data.sourceFiles[key];
      });

      await chapterSubmissionProgrammingAPI.createSubmission(
        token,
        chapterJid,
        problemJid,
        data.gradingLanguage,
        sourceFiles
      );

      toastActions.showSuccessToast('Solution submitted.');

      window.scrollTo(0, 0);
      dispatch(push(`/courses/${courseSlug}/chapters/${chapterAlias}/submissions`));
    };
  },
};
