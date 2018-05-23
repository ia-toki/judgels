import { AppState } from '../store';

export function selectStatementLanguage(state: AppState) {
  return state.webPrefs.statementLanguage;
}
