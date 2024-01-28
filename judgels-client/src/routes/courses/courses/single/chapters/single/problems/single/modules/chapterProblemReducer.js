export const initialState = {
  reloadKey: undefined,
};

export function ReloadChapterProblem({ reloadKey }) {
  return {
    type: 'jerahmeel/chapterProblem/RELOAD',
    payload: { reloadKey },
  };
}

export default function chapterProblemReducer(state = initialState, action) {
  switch (action.type) {
    case 'jerahmeel/chapterProblem/RELOAD':
      return action.payload;
    default:
      return state;
  }
}
