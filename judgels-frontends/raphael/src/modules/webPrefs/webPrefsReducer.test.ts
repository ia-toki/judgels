import { webPrefsReducer, WebPrefsState, INITIAL_STATE, PutStatementLanguage } from './webPrefsReducer';

describe('webPrefsReducer', () => {
  test('PUT_STATEMENT_LANGUAGE', () => {
    const state = INITIAL_STATE;
    const action = PutStatementLanguage.create('id');
    const nextState: WebPrefsState = { statementLanguage: 'id' };
    expect(webPrefsReducer(state, action)).toEqual(nextState);
  });

  test('other actions', () => {
    const state: WebPrefsState = { statementLanguage: 'en' };
    expect(webPrefsReducer(state, { type: 'other' })).toEqual(state);
  });

  test('initial state', () => {
    expect(webPrefsReducer(undefined as any, { type: 'other' })).toEqual(INITIAL_STATE);
  });
});
