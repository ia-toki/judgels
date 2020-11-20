import { user } from '../../fixtures/state';

import sessionReducer, { DelSession, PutToken, PutUser } from './sessionReducer';

describe('sessionReducer', () => {
  test('PUT_TOKEN', () => {
    const state = {};
    const action = PutToken('token123');
    const nextState = {
      token: 'token123',
    };
    expect(sessionReducer(state, action)).toEqual(nextState);
  });

  test('PUT_USER', () => {
    const state = {};
    const action = PutUser(user);
    const nextState = {
      user: user,
    };
    expect(sessionReducer(state, action)).toEqual(nextState);
  });

  test('DEL', () => {
    const state = {
      user: user,
      token: 'token123',
    };
    const action = DelSession();
    expect(sessionReducer(state, action)).toEqual({ user: undefined, token: undefined });
  });

  test('other actions', () => {
    const state = {
      user: user,
      token: 'token123',
    };
    expect(sessionReducer(state, { type: 'other' })).toEqual(state);
  });

  test('initial state', () => {
    expect(sessionReducer(undefined, { type: 'other' })).toEqual({ user: undefined, token: undefined });
  });
});
