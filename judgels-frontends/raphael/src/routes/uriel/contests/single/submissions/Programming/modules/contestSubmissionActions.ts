import { push } from 'react-router-redux';

import { selectToken } from 'modules/session/sessionSelectors';
import { NotFoundError } from 'modules/api/error';
import { ProblemSubmissionFormData } from 'components/ProblemWorksheetCard/Programming/ProblemSubmissionForm/ProblemSubmissionForm';

export const contestSubmissionActions = {
  getSubmissions: (contestJid: string, userJid?: string, problemJid?: string, page?: number) => {
    return async (dispatch, getState, { contestSubmissionProgrammingAPI }) => {
      const token = selectToken(getState());
      return await contestSubmissionProgrammingAPI.getSubmissions(token, contestJid, userJid, problemJid, page);
    };
  },

  getSubmissionWithSource: (contestJid: string, submissionId: number, language?: string) => {
    return async (dispatch, getState, { contestSubmissionProgrammingAPI }) => {
      const token = selectToken(getState());
      const submissionWithSource = await contestSubmissionProgrammingAPI.getSubmissionWithSource(
        token,
        submissionId,
        language
      );
      if (contestJid !== submissionWithSource.data.submission.containerJid) {
        throw new NotFoundError();
      }
      return submissionWithSource;
    };
  },

  createSubmission: (contestJid: string, contestSlug: string, problemJid: string, data: ProblemSubmissionFormData) => {
    return async (dispatch, getState, { contestSubmissionProgrammingAPI, toastActions }) => {
      const token = selectToken(getState());
      let sourceFiles = {};
      Object.keys(data.sourceFiles).forEach(key => {
        sourceFiles['sourceFiles.' + key] = data.sourceFiles[key];
      });

      await contestSubmissionProgrammingAPI.createSubmission(
        token,
        contestJid,
        problemJid,
        data.gradingLanguage,
        sourceFiles
      );

      toastActions.showSuccessToast('Solution submitted.');

      window.scrollTo(0, 0);
      dispatch(push(`/contests/${contestSlug}/submissions`));
    };
  },

  regradeSubmissions: (submissionJids: string[]) => {
    return async (dispatch, getState, { contestSubmissionProgrammingAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestSubmissionProgrammingAPI.regradeSubmissions(token, submissionJids);

      toastActions.showSuccessToast('Regrade request submitted.');
    };
  },

  regradeAllSubmissions: (contestJid: string, userJid?: string, problemJid?: string) => {
    return async (dispatch, getState, { contestSubmissionProgrammingAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestSubmissionProgrammingAPI.regradeAllSubmissions(token, contestJid, userJid, problemJid);

      toastActions.showSuccessToast('Regrade request submitted.');
    };
  },
};
