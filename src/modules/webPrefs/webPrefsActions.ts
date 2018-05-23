import { PutStatementLanguage } from './webPrefsReducer';
import { statementLanguageDisplayNamesMap } from '../api/sandalphon/language';

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
