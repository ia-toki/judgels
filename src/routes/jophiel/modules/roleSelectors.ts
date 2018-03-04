import { createSelector } from 'reselect';

import { AppState } from '../../../modules/store';

export const selectRole = createSelector([(state: AppState) => state.jophiel.role.value], value => value);
