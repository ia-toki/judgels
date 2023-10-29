export const initialState = {
  refreshKey: undefined,
};

export function RefreshChapterProblem(key) {
  return {
    type: 'jerahmeel/chapterProblem/REFRESH',
    payload: key,
  };
}

export default function chapterProblemReducer(state = initialState, action) {
  switch (action.type) {
    case 'jerahmeel/chapterProblem/REFRESH':
      return { ...state, refreshKey: action.payload };
    default:
      return state;
  }
}
