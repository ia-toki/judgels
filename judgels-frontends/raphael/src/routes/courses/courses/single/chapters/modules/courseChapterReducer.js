export const initialState = {
  value: undefined,
  courseSlug: undefined,
  name: undefined,
};

export function PutCourseChapter({ value, courseSlug, name }) {
  return {
    type: 'jerahmeel/courseChapter/PUT',
    payload: { value, courseSlug, name },
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
      return action.payload;
    case 'jerahmeel/courseChapter/DEL':
      return { value: undefined, courseSlug: undefined, name: undefined };
    default:
      return state;
  }
}
