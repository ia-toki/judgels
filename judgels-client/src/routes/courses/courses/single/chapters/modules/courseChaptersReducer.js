export const initialState = {
  value: undefined,
};

export function PutCourseChapters(chapters) {
  return {
    type: 'jerahmeel/courseChapters/PUT',
    payload: { chapters },
  };
}

export default function courseChaptersReducer(state = initialState, action) {
  switch (action.type) {
    case 'jerahmeel/courseChapters/PUT':
      return { ...state, value: action.payload };
    default:
      return state;
  }
}
