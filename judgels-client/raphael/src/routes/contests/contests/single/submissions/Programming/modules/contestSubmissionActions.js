import { push } from 'connected-react-router';

import { selectToken } from '../../../../../../../modules/session/sessionSelectors';
import { NotFoundError } from '../../../../../../../modules/api/error';
import { contestSubmissionProgrammingAPI } from '../../../../../../../modules/api/uriel/contestSubmissionProgramming';
import * as toastActions from '../../../../../../../modules/toast/toastActions';

export function getSubmissions(contestJid, username, problemAlias, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestSubmissionProgrammingAPI.getSubmissions(token, contestJid, username, problemAlias, page);
  };
}

export function getSubmissionWithSource(contestJid, submissionId, language) {
  return async (dispatch, getState) => {
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
}

export function createSubmission(contestJid, contestSlug, problemJid, data) {
  return async (dispatch, getState) => {
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
}

export function regradeSubmission(submissionJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestSubmissionProgrammingAPI.regradeSubmission(token, submissionJid);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}

export function regradeSubmissions(contestJid, username, problemAlias) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestSubmissionProgrammingAPI.regradeSubmissions(token, contestJid, username, problemAlias);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}

export function downloadSubmission(submissionJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestSubmissionProgrammingAPI.downloadSubmission(token, submissionJid);
  };
}
