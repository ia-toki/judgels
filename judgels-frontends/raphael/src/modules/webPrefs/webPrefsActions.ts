import { statementLanguageDisplayNamesMap } from 'modules/api/sandalphon/language';

import { PutStatementLanguage } from './webPrefsReducer';

export const webPrefsActions = {
  switchStatementLanguage: (language: string) => {
    return async (dispatch, getState, { toastActions }) => {
      dispatch(PutStatementLanguage.create(language));
      toastActions.showSuccessToast(
        'Switched default statement language to ' + statementLanguageDisplayNamesMap[language] + '.'
      );
    };
  },
};
