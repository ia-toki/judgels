import { push } from 'react-router-redux';

import { selectToken } from 'modules/session/sessionSelectors';
import { NotFoundError } from 'modules/api/error';
import { ProgrammingProblemSubmissionFormData } from 'components/ProblemWorksheetCard/ProgrammingProblemWorksheetCard/ProgrammingProblemSubmissionForm/ProgrammingProblemSubmissionForm';

export const contestProgrammingSubmissionActions = {
  getSubmissions: (contestJid: string, userJid?: string, problemJid?: string, page?: number) => {
    return async (dispatch, getState, { contestProgrammingSubmissionAPI }) => {
      const token = selectToken(getState());
      return await contestProgrammingSubmissionAPI.getSubmissions(token, contestJid, userJid, problemJid, page);
    };
  },

  getSubmissionWithSource: (contestJid: string, submissionId: number, language?: string) => {
    return async (dispatch, getState, { contestProgrammingSubmissionAPI }) => {
      const token = selectToken(getState());
      const submissionWithSource = await contestProgrammingSubmissionAPI.getSubmissionWithSource(
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

  createSubmission: (
    contestJid: string,
    contestSlug: string,
    problemJid: string,
    data: ProgrammingProblemSubmissionFormData
  ) => {
    return async (dispatch, getState, { contestProgrammingSubmissionAPI, toastActions }) => {
      const token = selectToken(getState());
      let sourceFiles = {};
      Object.keys(data.sourceFiles).forEach(key => {
        sourceFiles['sourceFiles.' + key] = data.sourceFiles[key];
      });

      await contestProgrammingSubmissionAPI.createSubmission(
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
    return async (dispatch, getState, { contestProgrammingSubmissionAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestProgrammingSubmissionAPI.regradeSubmissions(token, submissionJids);

      toastActions.showSuccessToast('Regrade request submitted.');
    };
  },

  regradeAllSubmissions: (contestJid: string, userJid?: string, problemJid?: string) => {
    return async (dispatch, getState, { contestProgrammingSubmissionAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestProgrammingSubmissionAPI.regradeAllSubmissions(token, contestJid, userJid, problemJid);

      toastActions.showSuccessToast('Regrade request submitted.');
    };
  },
};
