import webPrefsReducer, {
  PutStatementLanguage,
  PutGradingLanguage,
  PutEditorialLanguage,
  PutIsDarkMode,
} from './webPrefsReducer';

describe('webPrefsReducer', () => {
  test('PUT_IS_DARK_MODE', () => {
    const state = { isDarkMode: false };
    const action = PutIsDarkMode(true);
    const nextState = { isDarkMode: true };
    expect(webPrefsReducer(state, action)).toEqual(nextState);
  });

  test('PUT_STATEMENT_LANGUAGE', () => {
    const state = { statementLanguage: 'en', gradingLanguage: 'Cpp17' };
    const action = PutStatementLanguage('id');
    const nextState = { statementLanguage: 'id', gradingLanguage: 'Cpp17' };
    expect(webPrefsReducer(state, action)).toEqual(nextState);
  });

  test('PUT_EDITORIAL_LANGUAGE', () => {
    const state = { editorialLanguage: 'en', gradingLanguage: 'Cpp17' };
    const action = PutEditorialLanguage('id');
    const nextState = { editorialLanguage: 'id', gradingLanguage: 'Cpp17' };
    expect(webPrefsReducer(state, action)).toEqual(nextState);
  });

  test('PUT_GRADING_LANGUAGE', () => {
    const state = { statementLanguage: 'en', gradingLanguage: 'Cpp17' };
    const action = PutGradingLanguage('Java');
    const nextState = { statementLanguage: 'en', gradingLanguage: 'Java' };
    expect(webPrefsReducer(state, action)).toEqual(nextState);
  });

  test('other actions', () => {
    const state = { statementLanguage: 'en', gradingLanguage: 'Cpp17' };
    expect(webPrefsReducer(state, { type: 'other' })).toEqual(state);
  });

  test('initial state', () => {
    expect(webPrefsReducer(undefined, { type: 'other' })).toEqual({
      statementLanguage: 'id',
      editorialLanguage: 'id',
      gradingLanguage: 'Cpp17',
    });
  });
});
