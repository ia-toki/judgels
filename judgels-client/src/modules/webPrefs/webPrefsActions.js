import { languageDisplayNamesMap } from '../api/sandalphon/language';
import {
  PutEditorialLanguage,
  PutGradingLanguage,
  PutIsDarkMode,
  PutShowProblemDifficulty,
  PutShowProblemTopicTags,
  PutStatementLanguage,
} from './webPrefsReducer';

import * as toastActions from '../toast/toastActions';

export const switchDarkMode = PutIsDarkMode;
export const switchShowProblemDifficulty = PutShowProblemDifficulty;
export const switchShowProblemTopicTags = PutShowProblemTopicTags;

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
