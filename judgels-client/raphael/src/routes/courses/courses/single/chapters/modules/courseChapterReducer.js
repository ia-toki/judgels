export const initialState = {
  value: undefined,
};

export function PutCourseChapter(chapter) {
  return {
    type: 'jerahmeel/courseChapter/PUT',
    payload: chapter,
  };
}

export function DelCourseChapter() {
  return {
    type: 'jerahmeel/courseChapter/DEL',
  };
}

export default function courseChapterReducer(state = initialState, action) {
  switch (action.type) {
    case 'jerahmeel/courseChapter/PUT':
      return { ...state, value: action.payload };
    case 'jerahmeel/courseChapter/DEL':
      return { value: undefined };
    default:
      return state;
  }
}
