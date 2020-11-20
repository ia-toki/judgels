export const initialState = {
  config: {
    announcements: [],
  },
};

export function PutWebConfig(config) {
  return {
    type: 'jophiel/web/PUT_CONFIG',
    payload: config,
  };
}

export default function webReducer(state = initialState, action) {
  switch (action.type) {
    case 'jophiel/web/PUT_CONFIG':
      return { config: action.payload };
    default:
      return state;
  }
}
