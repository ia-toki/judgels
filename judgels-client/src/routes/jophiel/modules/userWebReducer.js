const initialState = {
  config: {
    role: {},
    announcements: [],
    isConfigLoaded: false,
  },
};

export function PutWebConfig(config) {
  return {
    type: 'jophiel/userWeb/PUT_CONFIG',
    payload: config,
  };
}

export default function userWebReducer(state = initialState, action) {
  switch (action.type) {
    case 'jophiel/userWeb/PUT_CONFIG':
      return { config: action.payload, isConfigLoaded: true };
    default:
      return state;
  }
}
