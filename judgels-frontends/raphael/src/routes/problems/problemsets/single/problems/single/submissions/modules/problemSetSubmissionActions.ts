import { push } from 'connected-react-router';

import { ProblemSubmissionFormData } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionForm/ProblemSubmissionForm';

export const problemSetSubmissionActions = {
  createSubmission: (
    problemSetSlug: string,
    problemSetJid: string,
    problemAlias: string,
    problemJid: string,
    data: ProblemSubmissionFormData
  ) => {
    return async (dispatch, getState, { toastActions }) => {
      toastActions.showSuccessToast('Solution submitted.');

      window.scrollTo(0, 0);
      dispatch(push(`/problems/${problemSetSlug}/${problemAlias}/submissions`));
    };
  },
};
