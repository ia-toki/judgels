import { push } from 'connected-react-router';

import { selectToken } from '../../../../../../../modules/session/sessionSelectors';
import { NotFoundError } from '../../../../../../../modules/api/error';
import { ProblemSubmissionFormData } from '../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionForm/ProblemSubmissionForm';
import { contestSubmissionProgrammingAPI } from '../../../../../../../modules/api/uriel/contestSubmissionProgramming';
import * as toastActions from '../../../../../../../modules/toast/toastActions';

export function getSubmissions(contestJid: string, userJid?: string, problemJid?: string, page?: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestSubmissionProgrammingAPI.getSubmissions(token, contestJid, userJid, problemJid, page);
  };
}

export function getSubmissionWithSource(contestJid: string, submissionId: number, language?: string) {
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

export function createSubmission(
  contestJid: string,
  contestSlug: string,
  problemJid: string,
  data: ProblemSubmissionFormData
) {
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

export function regradeSubmission(submissionJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestSubmissionProgrammingAPI.regradeSubmission(token, submissionJid);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}

export function regradeSubmissions(contestJid: string, userJid?: string, problemJid?: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestSubmissionProgrammingAPI.regradeSubmissions(token, contestJid, userJid, problemJid);

    toastActions.showSuccessToast('Regrade in progress.');
  };
}
