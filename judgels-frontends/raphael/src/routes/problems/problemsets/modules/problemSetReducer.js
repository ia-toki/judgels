export const initialState = {
  value: undefined,
};

export function PutProblemSet(problemSet) {
  return {
    type: 'jerahmeel/problemSet/PUT',
    payload: problemSet,
  };
}

export function DelProblemSet() {
  return {
    type: 'jerahmeel/problemSet/DEL',
  };
}

export default function problemSetReducer(state = initialState, action) {
  switch (action.type) {
    case 'jerahmeel/problemSet/PUT':
      return { value: action.payload };
    case 'jerahmeel/problemSet/DEL':
      return { value: undefined };
    default:
      return state;
  }
}
