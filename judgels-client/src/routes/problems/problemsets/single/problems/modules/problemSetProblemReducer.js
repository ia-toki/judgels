export const initialState = {
  value: undefined,
};

export function PutProblemSetProblem(problemSetProblem) {
  return {
    type: 'jerahmeel/problemSetProblem/PUT',
    payload: problemSetProblem,
  };
}

export function DelProblemSetProblem() {
  return {
    type: 'jerahmeel/problemSetProblem/DEL',
  };
}

export default function problemSetProblemReducer(state = initialState, action) {
  switch (action.type) {
    case 'jerahmeel/problemSetProblem/PUT':
      return { value: action.payload };
    case 'jerahmeel/problemSetProblem/DEL':
      return { value: undefined };
    default:
      return state;
  }
}
