const initialState = {
  token: undefined,
  user: undefined,
};

export function PutToken(token) {
  return {
    type: 'session/PUT_TOKEN',
    payload: token,
  };
}

export function PutUser(user) {
  return {
    type: 'session/PUT_USER',
    payload: user,
  };
}

export function DelSession() {
  return {
    type: 'session/DEL',
  };
}

export default function sessionReducer(state = initialState, action) {
  switch (action.type) {
    case 'session/PUT_TOKEN':
      return { ...state, token: action.payload };
    case 'session/PUT_USER':
      return { ...state, user: action.payload };
    case 'session/DEL':
      return initialState;
    default:
      return state;
  }
}
