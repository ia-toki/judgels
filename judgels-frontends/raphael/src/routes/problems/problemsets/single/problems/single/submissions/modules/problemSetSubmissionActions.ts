import { push } from 'connected-react-router';

import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { ProblemSubmissionFormData } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionForm/ProblemSubmissionForm';
import { NotFoundError } from '../../../../../../../../modules/api/error';

export const problemSetSubmissionActions = {
  getSubmissions: (problemSetJid: string, userJid?: string, problemJid?: string, page?: number) => {
    return async (dispatch, getState, { problemSetSubmissionProgrammingAPI }) => {
      const token = selectToken(getState());
      return await problemSetSubmissionProgrammingAPI.getSubmissions(token, problemSetJid, userJid, problemJid, page);
    };
  },

  getSubmissionWithSource: (problemSetJid: string, submissionId: number, language?: string) => {
    return async (dispatch, getState, { problemSetSubmissionProgrammingAPI }) => {
      const token = selectToken(getState());
      const submissionWithSource = await problemSetSubmissionProgrammingAPI.getSubmissionWithSource(
        token,
        submissionId,
        language
      );
      if (problemSetJid !== submissionWithSource.data.submission.containerJid) {
        throw new NotFoundError();
      }
      return submissionWithSource;
    };
  },

  createSubmission: (
    problemSetSlug: string,
    problemSetJid: string,
    problemAlias: string,
    problemJid: string,
    data: ProblemSubmissionFormData
  ) => {
    return async (dispatch, getState, { problemSetSubmissionProgrammingAPI, toastActions }) => {
      const token = selectToken(getState());
      let sourceFiles = {};
      Object.keys(data.sourceFiles).forEach(key => {
        sourceFiles['sourceFiles.' + key] = data.sourceFiles[key];
      });

      await problemSetSubmissionProgrammingAPI.createSubmission(
        token,
        problemSetJid,
        problemJid,
        data.gradingLanguage,
        sourceFiles
      );

      toastActions.showSuccessToast('Solution submitted.');

      window.scrollTo(0, 0);
      dispatch(push(`/problems/${problemSetSlug}/${problemAlias}/submissions`));
    };
  },

  regradeSubmission: (submissionJid: string) => {
    return async (dispatch, getState, { problemSetSubmissionProgrammingAPI, toastActions }) => {
      const token = selectToken(getState());
      await problemSetSubmissionProgrammingAPI.regradeSubmission(token, submissionJid);

      toastActions.showSuccessToast('Regrade in progress.');
    };
  },

  regradeSubmissions: (problemSetJid: string, userJid?: string, problemJid?: string) => {
    return async (dispatch, getState, { problemSetSubmissionProgrammingAPI, toastActions }) => {
      const token = selectToken(getState());
      await problemSetSubmissionProgrammingAPI.regradeSubmissions(token, problemSetJid, userJid, problemJid);

      toastActions.showSuccessToast('Regrade in progress.');
    };
  },
};
