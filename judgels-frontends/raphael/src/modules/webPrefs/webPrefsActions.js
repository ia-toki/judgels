import { statementLanguageDisplayNamesMap } from '../api/sandalphon/language';
import { PutStatementLanguage, PutGradingLanguage } from './webPrefsReducer';
import * as toastActions from '../toast/toastActions';

export function switchStatementLanguage(language) {
  return async dispatch => {
    dispatch(PutStatementLanguage(language));
    toastActions.showSuccessToast(
      'Switched default statement language to ' + statementLanguageDisplayNamesMap[language] + '.'
    );
  };
}

export function updateGradingLanguage(language) {
  return async dispatch => {
    dispatch(PutGradingLanguage(language));
  };
}
