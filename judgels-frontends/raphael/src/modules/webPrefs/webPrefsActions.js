import { languageDisplayNamesMap } from '../api/sandalphon/language';
import { PutStatementLanguage, PutEditorialLanguage, PutGradingLanguage, PutIsDarkMode } from './webPrefsReducer';
import * as toastActions from '../toast/toastActions';

export function switchDarkMode(isDarkMode) {
  return async dispatch => {
    dispatch(PutIsDarkMode(isDarkMode));
  };
}

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
