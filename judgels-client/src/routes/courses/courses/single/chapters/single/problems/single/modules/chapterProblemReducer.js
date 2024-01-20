export const initialState = {
  refreshKey: undefined,
  shouldScrollToEditorial: false,
};

export function RefreshChapterProblem({ refreshKey, shouldScrollToEditorial }) {
  return {
    type: 'jerahmeel/chapterProblem/REFRESH',
    payload: { refreshKey, shouldScrollToEditorial },
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
