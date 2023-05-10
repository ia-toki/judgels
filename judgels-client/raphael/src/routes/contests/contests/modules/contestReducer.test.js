import contestReducer, { DelContest, PutContest } from './contestReducer';

describe('contestReducer', () => {
  test('PUT', () => {
    const state = {};
    const contest = { name: 'contest ' };
    const action = PutContest(contest);
    const nextState = { value: contest };
    expect(contestReducer(state, action)).toEqual(nextState);
  });

  test('DEL', () => {
    const contest = { name: 'contest ' };
    const state = { value: contest };
    const action = DelContest();
    const nextState = {};
    expect(contestReducer(state, action)).toEqual(nextState);
  });

  test('other actions', () => {
    const contest = { name: 'contest ' };
    const state = { value: contest };
    expect(contestReducer(state, { type: 'other' })).toEqual(state);
  });

  test('initial state', () => {
    expect(contestReducer(undefined, { type: 'other' })).toEqual({ value: undefined, isEditing: false });
  });
});
