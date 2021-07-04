export function selectIsNewToDarkMode(state) {
  return state.webPrefs.isDarkMode === undefined;
}

export function selectIsDarkMode(state) {
  return !!state.webPrefs.isDarkMode;
}

export function selectShowProblemDifficulty(state) {
  return !state.webPrefs.hideProblemDifficulty;
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
