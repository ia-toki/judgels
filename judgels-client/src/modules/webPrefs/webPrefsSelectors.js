export function selectIsDarkMode(state) {
  return !!state.webPrefs?.isDarkMode;
}

export function selectShowProblemDifficulty(state) {
  return !state.webPrefs.hideProblemDifficulty;
}

export function selectShowProblemTopicTags(state) {
  return !!state.webPrefs.showProblemTopicTags;
}

export function selectStatementLanguage(state) {
  return state.webPrefs.statementLanguage;
}

export function selectEditorialLanguage(state) {
  return state.webPrefs.editorialLanguage;
}

export function selectGradingLanguage(state) {
  return state.webPrefs.gradingLanguage;
}
