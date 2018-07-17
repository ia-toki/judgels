import { setWith, TypedAction, TypedReducer } from 'redoodle';

export interface WebPrefsState {
  statementLanguage: string;
}

export const INITIAL_STATE: WebPrefsState = { statementLanguage: 'id' };

export const PutStatementLanguage = TypedAction.define('webPrefs/PUT_STATEMENT_LANGUAGE')<string>();

function createWebPrefsReducer() {
  const builder = TypedReducer.builder<WebPrefsState>();

  builder.withHandler(PutStatementLanguage.TYPE, (state, statementLanguage) => setWith(state, { statementLanguage }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const webPrefsReducer = createWebPrefsReducer();
