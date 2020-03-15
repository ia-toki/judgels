import { statementLanguageDisplayNamesMap } from '../../modules/api/sandalphon/language';
import { PutStatementLanguage, PutGradingLanguage } from './webPrefsReducer';

export function switchStatementLanguage(language: string) {
  return async (dispatch, getState, { toastActions }) => {
    dispatch(PutStatementLanguage.create(language));
    toastActions.showSuccessToast(
      'Switched default statement language to ' + statementLanguageDisplayNamesMap[language] + '.'
    );
  };
}

export function updateGradingLanguage(language: string) {
  return async dispatch => {
    dispatch(PutGradingLanguage.create(language));
  };
}
