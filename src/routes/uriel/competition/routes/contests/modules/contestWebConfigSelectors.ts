import { AppState } from '../../../../../../modules/store';

export function selectContestWebConfig(state: AppState) {
  return state.uriel.contestWebConfig.value;
}
