import { DelSession, INITIAL_STATE, sessionReducer, SessionState, PutToken, PutUser } from './sessionReducer';

describe('sessionReducer', () => {
  test('PUT_TOKEN', () => {
    const state = INITIAL_STATE;
    const action = PutToken.create('token123');
    const nextState: SessionState = {
      isLoggedIn: true,
      token: 'token123',
    };
    expect(sessionReducer(state, action)).toEqual(nextState);
  });

  test('PUT_USER', () => {
    const state = INITIAL_STATE;
    const action = PutUser.create({ jid: 'jid123', username: 'user' });
    const nextState: SessionState = {
      isLoggedIn: false,
      user: { jid: 'jid123', username: 'user' },
    };
    expect(sessionReducer(state, action)).toEqual(nextState);
  });

  test('DEL', () => {
    const state: SessionState = {
      isLoggedIn: true,
      user: { jid: 'jid123', username: 'user' },
      token: 'token123',
    };
    const action = DelSession.create();
    expect(sessionReducer(state, action)).toEqual(INITIAL_STATE);
  });

  test('other actions', () => {
    const state: SessionState = {
      isLoggedIn: true,
      user: { jid: 'jid123', username: 'user' },
      token: 'token123',
    };
    expect(sessionReducer(state, { type: 'other' })).toEqual(state);
  });

  test('initial state', () => {
    expect(sessionReducer(undefined as any, { type: 'other' })).toEqual(INITIAL_STATE);
  });
});
