import { ContestTab } from '../../../../modules/api/uriel/contestWeb';

import contestWebConfigReducer, { DelWebConfig, PutWebConfig } from './contestWebConfigReducer';

describe('contestWebConfigReducer', () => {
  test('PUT', () => {
    const state = {};
    const webConfig = { visibleTabs: [ContestTab.Scoreboard] };
    const action = PutWebConfig(webConfig);
    const nextState = { value: webConfig };
    expect(contestWebConfigReducer(state, action)).toEqual(nextState);
  });

  test('DEL', () => {
    const webConfig = { visibleTabs: [ContestTab.Scoreboard] };
    const state = { value: webConfig };
    const action = DelWebConfig();
    const nextState = {};
    expect(contestWebConfigReducer(state, action)).toEqual(nextState);
  });

  test('other actions', () => {
    const webConfig = { visibleTabs: [ContestTab.Scoreboard] };
    const state = { value: webConfig };
    expect(contestWebConfigReducer(state, { type: 'other' })).toEqual(state);
  });

  test('initial state', () => {
    expect(contestWebConfigReducer(undefined, { type: 'other' })).toEqual({ value: undefined });
  });
});
