export function PutUser({ userJid, username }) {
  return {
    type: 'jophiel/profile/PUT',
    payload: {
      userJid,
      username,
    },
  };
}
export function DelUser() {
  return {
    type: 'jophiel/profile/DEL',
  };
}

export default function profileReducer(state = {}, action) {
  switch (action.type) {
    case 'jophiel/profile/PUT':
      return action.payload;
    case 'jophiel/profile/DEL':
      return { userJid: undefined, username: undefined };
    default:
      return state;
  }
}
