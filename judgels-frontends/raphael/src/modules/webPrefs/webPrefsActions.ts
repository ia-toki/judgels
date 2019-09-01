import { statementLanguageDisplayNamesMap } from '../../modules/api/sandalphon/language';

import { PutStatementLanguage, PutGradingLanguage } from './webPrefsReducer';

export const webPrefsActions = {
  switchStatementLanguage: (language: string) => {
    return async (dispatch, getState, { toastActions }) => {
      dispatch(PutStatementLanguage.create(language));
      toastActions.showSuccessToast(
        'Switched default statement language to ' + statementLanguageDisplayNamesMap[language] + '.'
      );
    };
  },

  updateGradingLanguage: (language: string) => {
    return async (dispatch, getState, { toastActions }) => {
      dispatch(PutGradingLanguage.create(language));
    };
  },
};
