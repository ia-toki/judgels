import { AppState } from 'modules/store';

export function selectStatementLanguage(state: AppState) {
  return state.webPrefs.statementLanguage;
}
