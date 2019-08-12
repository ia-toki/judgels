import { setWith, TypedAction, TypedReducer } from 'redoodle';

export interface WebPrefsState {
  statementLanguage: string;
  gradingLanguage: string;
}

export const INITIAL_STATE: WebPrefsState = { statementLanguage: 'id', gradingLanguage: 'Cpp11' };

export const PutStatementLanguage = TypedAction.define('webPrefs/PUT_STATEMENT_LANGUAGE')<string>();
export const PutGradingLanguage = TypedAction.define('webPrefs/PUT_GRADING_LANGUAGE')<string>();

function createWebPrefsReducer() {
  const builder = TypedReducer.builder<WebPrefsState>();

  builder.withHandler(PutStatementLanguage.TYPE, (state, statementLanguage) => setWith(state, { statementLanguage }));
  builder.withHandler(PutGradingLanguage.TYPE, (state, gradingLanguage) => setWith(state, { gradingLanguage }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const webPrefsReducer = createWebPrefsReducer();
