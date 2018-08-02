import { ContestTab, ContestWebConfig } from 'modules/api/uriel/contestWeb';

import {
  contestWebConfigReducer,
  ContestWebConfigState,
  DelWebConfig,
  INITIAL_STATE,
  PutWebConfig,
} from './contestWebConfigReducer';

describe('contestWebConfigReducer', () => {
  test('PUT', () => {
    const state = INITIAL_STATE;
    const webConfig = { visibleTabs: [ContestTab.Scoreboard] } as ContestWebConfig;
    const action = PutWebConfig.create(webConfig);
    const nextState: ContestWebConfigState = { value: webConfig };
    expect(contestWebConfigReducer(state, action)).toEqual(nextState);
  });

  test('DEL', () => {
    const webConfig = { visibleTabs: [ContestTab.Scoreboard] } as ContestWebConfig;
    const state: ContestWebConfigState = { value: webConfig };
    const action = DelWebConfig.create();
    const nextState: ContestWebConfigState = {};
    expect(contestWebConfigReducer(state, action)).toEqual(nextState);
  });

  test('other actions', () => {
    const webConfig = { visibleTabs: [ContestTab.Scoreboard] } as ContestWebConfig;
    const state: ContestWebConfigState = { value: webConfig };
    expect(contestWebConfigReducer(state, { type: 'other' })).toEqual(state);
  });

  test('initial state', () => {
    expect(contestWebConfigReducer(undefined as any, { type: 'other' })).toEqual(INITIAL_STATE);
  });
});
