export const initialState = {
  value: undefined,
  isEditing: false,
};

export function PutContest(contest) {
  return {
    type: 'uriel/contest/PUT',
    payload: contest,
  };
}

export function DelContest() {
  return {
    type: 'uriel/contest/DEL',
  };
}

export function EditContest(isEditing) {
  return {
    type: 'uriel/contest/EDIT',
    payload: isEditing,
  };
}

export default function contestReducer(state = initialState, action) {
  switch (action.type) {
    case 'uriel/contest/PUT':
      return { ...state, value: action.payload };
    case 'uriel/contest/DEL':
      return { value: undefined };
    case 'uriel/contest/EDIT':
      return { ...state, isEditing: action.payload };
    default:
      return state;
  }
}
