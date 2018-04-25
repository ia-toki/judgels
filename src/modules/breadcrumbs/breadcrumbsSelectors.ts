import { createSelector } from 'reselect';

import { APP_CONFIG } from '../../conf';
import { AppState } from '../store';

export function selectSortedBreadcrumbs(state: AppState) {
  return state.breadcrumbs.values.slice().sort((a, b) => a.link.length - b.link.length);
}

export const selectDocumentTitle = createSelector([selectSortedBreadcrumbs], breadcrumbs => {
  let title = APP_CONFIG.name;
  if (breadcrumbs.length) {
    title = `${breadcrumbs[breadcrumbs.length - 1].title} | ${title}`;
  }
  return title;
});
