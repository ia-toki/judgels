import { AppState } from 'modules/store';

export function selectStatementLanguage(state: AppState) {
  return state.webPrefs.statementLanguage;
}

export function selectGradingLanguage(state: AppState) {
  return state.webPrefs.gradingLanguage;
}
