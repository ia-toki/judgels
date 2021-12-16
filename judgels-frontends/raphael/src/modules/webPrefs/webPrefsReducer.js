const initialState = {
  isDarkMode: undefined,
  hideProblemDifficulty: undefined,
  showProblemTopicTags: undefined,
  statementLanguage: 'id',
  editorialLanguage: 'id',
  gradingLanguage: 'Cpp20',
};

export function PutIsDarkMode(val) {
  return {
    type: 'webPrefs/PUT_IS_DARK_MODE',
    payload: val,
  };
}

export function PutShowProblemDifficulty(val) {
  return {
    type: 'webPrefs/PUT_SHOW_PROBLEM_DIFFICULTY',
    payload: val,
  };
}

export function PutShowProblemTopicTags(val) {
  return {
    type: 'webPrefs/PUT_SHOW_PROBLEM_TOPIC_TAGS',
    payload: val,
  };
}

export function PutStatementLanguage(lang) {
  return {
    type: 'webPrefs/PUT_STATEMENT_LANGUAGE',
    payload: lang,
  };
}

export function PutEditorialLanguage(lang) {
  return {
    type: 'webPrefs/PUT_EDITORIAL_LANGUAGE',
    payload: lang,
  };
}

export function PutGradingLanguage(lang) {
  return {
    type: 'webPrefs/PUT_GRADING_LANGUAGE',
    payload: lang,
  };
}

export default function webPrefsReducer(state = initialState, action) {
  switch (action.type) {
    case 'webPrefs/PUT_IS_DARK_MODE':
      return { ...state, isDarkMode: action.payload };
    case 'webPrefs/PUT_SHOW_PROBLEM_DIFFICULTY':
      return { ...state, hideProblemDifficulty: !action.payload };
    case 'webPrefs/PUT_SHOW_PROBLEM_TOPIC_TAGS':
      return { ...state, showProblemTopicTags: action.payload };
    case 'webPrefs/PUT_STATEMENT_LANGUAGE':
      return { ...state, statementLanguage: action.payload };
    case 'webPrefs/PUT_EDITORIAL_LANGUAGE':
      return { ...state, editorialLanguage: action.payload };
    case 'webPrefs/PUT_GRADING_LANGUAGE':
      return { ...state, gradingLanguage: action.payload };
    default:
      return state;
  }
}
