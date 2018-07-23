import { contestReducer, ContestState, DelContest, INITIAL_STATE, PutContest } from './contestReducer';
import { Contest } from '../../../../modules/api/uriel/contest';

describe('contestReducer', () => {
  test('PUT', () => {
    const state = INITIAL_STATE;
    const contest = { name: 'contest ' } as Contest;
    const action = PutContest.create(contest);
    const nextState: ContestState = { value: contest };
    expect(contestReducer(state, action)).toEqual(nextState);
  });

  test('DEL', () => {
    const contest = { name: 'contest ' } as Contest;
    const state: ContestState = { value: contest };
    const action = DelContest.create();
    const nextState: ContestState = {};
    expect(contestReducer(state, action)).toEqual(nextState);
  });

  test('other actions', () => {
    const contest = { name: 'contest ' } as Contest;
    const state: ContestState = { value: contest };
    expect(contestReducer(state, { type: 'other' })).toEqual(state);
  });

  test('initial state', () => {
    expect(contestReducer(undefined as any, { type: 'other' })).toEqual(INITIAL_STATE);
  });
});
