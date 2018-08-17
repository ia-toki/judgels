import { ContestDescription } from 'modules/api/uriel/contest';

import { 
    contestDescriptionReducer,
    ContestDescriptionState,
    DelContest,
    INITIAL_STATE,
    PutContest 
} from './contestDescriptionReducer';

describe('contestDescriptionReducer', () => {
  test('PUT', () => {
    const state = INITIAL_STATE;
    const contestDescription = { jid: 'contestDescription ' } as ContestDescription;
    const action = PutContest.create(contestDescription);
    const nextState: ContestDescriptionState = { value: contestDescription };
    expect(contestDescriptionReducer(state, action)).toEqual(nextState);
  });

  test('DEL', () => {
    const contestDescription = { jid: 'contestDescription ' } as ContestDescription;
    const state: ContestDescriptionState = { value: contestDescription };
    const action = DelContest.create();
    const nextState: ContestDescriptionState = {};
    expect(contestDescriptionReducer(state, action)).toEqual(nextState);
  });

  test('other actions', () => {
    const contestDescription = { jid: 'contestDescription ' } as ContestDescription;
    const state: ContestDescriptionState = { value: contestDescription };
    expect(contestDescriptionReducer(state, { type: 'other' })).toEqual(state);
  });

  test('initial state', () => {
    expect(contestDescriptionReducer(undefined as any, { type: 'other' })).toEqual(INITIAL_STATE);
  });
});
