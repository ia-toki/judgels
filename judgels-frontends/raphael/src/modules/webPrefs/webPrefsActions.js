import { languageDisplayNamesMap } from '../api/sandalphon/language';
import { PutStatementLanguage, PutEditorialLanguage, PutGradingLanguage } from './webPrefsReducer';
import * as toastActions from '../toast/toastActions';

export function switchStatementLanguage(language) {
  return async dispatch => {
    dispatch(PutStatementLanguage(language));
    toastActions.showSuccessToast('Switched default statement language to ' + languageDisplayNamesMap[language] + '.');
  };
}

export function switchEditorialLanguage(language) {
  return async dispatch => {
    dispatch(PutEditorialLanguage(language));
    toastActions.showSuccessToast('Switched default editorial language to ' + languageDisplayNamesMap[language] + '.');
  };
}

export function updateGradingLanguage(language) {
  return async dispatch => {
    dispatch(PutGradingLanguage(language));
  };
}
