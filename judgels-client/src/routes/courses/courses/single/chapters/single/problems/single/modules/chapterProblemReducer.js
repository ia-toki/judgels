export const initialState = {
  refreshKey: undefined,
  shouldScrollToEditorial: false,
  shouldResetEditor: false,
};

export function RefreshChapterProblem({ refreshKey, shouldScrollToEditorial, shouldResetEditor }) {
  return {
    type: 'jerahmeel/chapterProblem/REFRESH',
    payload: { refreshKey, shouldScrollToEditorial, shouldResetEditor },
  };
}

export default function chapterProblemReducer(state = initialState, action) {
  switch (action.type) {
    case 'jerahmeel/chapterProblem/REFRESH':
      return action.payload;
    default:
      return state;
  }
}
