export const initialState = {
  value: undefined,
};

export function PutWebConfig(config) {
  return {
    type: 'uriel/contest/web/PUT_CONFIG',
    payload: config,
  };
}

export function DelWebConfig() {
  return {
    type: 'uriel/contest/web/DEL_CONFIG',
  };
}

export default function contestWebConfigReducer(state = initialState, action) {
  switch (action.type) {
    case 'uriel/contest/web/PUT_CONFIG':
      return { value: action.payload };
    case 'uriel/contest/web/DEL_CONFIG':
      return { value: undefined };
    default:
      return state;
  }
}
