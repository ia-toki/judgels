import { push } from 'connected-react-router';

import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { ProblemSubmissionFormData } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionForm/ProblemSubmissionForm';

export const problemSetSubmissionActions = {
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
};
