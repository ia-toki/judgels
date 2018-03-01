import { createSelector } from 'reselect';

import { AppState } from '../../../../../../modules/store';

export const selectContest = createSelector([(state: AppState) => state.uriel.contest.value], value => value);
