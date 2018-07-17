import { selectToken } from '../../../../../../../../../../modules/session/sessionSelectors';
import { NotFoundError } from '../../../../../../../../../../modules/api/error';
import { ProblemSubmissionFormData } from '../../../../../../../../../../components/ProblemWorksheetCard/ProblemSubmissionForm/ProblemSubmissionForm';
import { push } from 'react-router-redux';

export const contestSubmissionActions = {
  fetchMyList: (contestJid: string, page: number) => {
    return async (dispatch, getState, { contestSubmissionAPI }) => {
      const token = selectToken(getState());
      return await contestSubmissionAPI.getMySubmissions(token, contestJid, page);
    };
  },

  fetchWithSource: (contestJid: string, submissionId: number, language: string) => {
    return async (dispatch, getState, { contestSubmissionAPI }) => {
      const token = selectToken(getState());
      const submissionWithSource = await contestSubmissionAPI.getSubmissionWithSource(token, submissionId, language);
      if (contestJid !== submissionWithSource.data.submission.containerJid) {
        throw new NotFoundError();
      }
      return submissionWithSource;
    };
  },

  submit: (contestJid: string, contestId: number, problemJid: string, data: ProblemSubmissionFormData) => {
    return async (dispatch, getState, { contestSubmissionAPI, toastActions }) => {
      const token = selectToken(getState());
      let sourceFiles = {};
      Object.keys(data.sourceFiles).forEach(key => {
        sourceFiles['sourceFiles.' + key] = data.sourceFiles[key];
      });

      await contestSubmissionAPI.createSubmission(token, contestJid, problemJid, data.gradingLanguage, sourceFiles);

      toastActions.showSuccessToast('Solution submitted.');

      window.scrollTo(0, 0);
      dispatch(push(`/competition/contests/${contestId}/submissions`));
    };
  },
};
