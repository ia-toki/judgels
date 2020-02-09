import { push } from 'connected-react-router';

import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { ProblemSubmissionFormData } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionForm/ProblemSubmissionForm';
import { submissionProgrammingAPI } from '../../../../../../../../modules/api/jerahmeel/submissionProgramming';
import { toastActions } from '../../../../../../../../modules/toast/toastActions';

export function getSubmissions(problemSetJid: string, userJid?: string, problemJid?: string, page?: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await submissionProgrammingAPI.getSubmissions(token, problemSetJid, userJid, problemJid, page);
  };
}

export function getSubmissionWithSource(submissionId: number, language?: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await submissionProgrammingAPI.getSubmissionWithSource(token, submissionId, language);
  };
}

export function createSubmission(
  problemSetSlug: string,
  problemSetJid: string,
  problemAlias: string,
  problemJid: string,
  data: ProblemSubmissionFormData
) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    let sourceFiles = {};
    Object.keys(data.sourceFiles).forEach(key => {
      sourceFiles['sourceFiles.' + key] = data.sourceFiles[key];
    });

    await submissionProgrammingAPI.createSubmission(
      token,
      problemSetJid,
      problemJid,
      data.gradingLanguage,
      sourceFiles
    );

    toastActions.showSuccessToast('Solution submitted.');

    window.scrollTo(0, 0);
    dispatch(push(`/problems/${problemSetSlug}/${problemAlias}/submissions/mine`));
  };
}

export function regradeSubmission(submissionJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await submissionProgrammingAPI.regradeSubmission(token, submissionJid);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}

export function regradeSubmissions(problemSetJid: string, userJid?: string, problemJid?: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await submissionProgrammingAPI.regradeSubmissions(token, problemSetJid, userJid, problemJid);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}
