import { APP_CONFIG } from '../../conf';
import { AppState } from '../../modules/store';

export function selectSortedBreadcrumbs(state: AppState) {
  return state.breadcrumbs.values.slice().sort((a, b) => a.link.length - b.link.length);
}

export function selectDocumentTitle(state: AppState) {
  let title = APP_CONFIG.name;
  let longestBreadcrumb;
  state.breadcrumbs.values.forEach(breadcrumb => {
    if (!longestBreadcrumb || breadcrumb.link.length > longestBreadcrumb.link.length) {
      longestBreadcrumb = breadcrumb;
    }
  });

  if (longestBreadcrumb) {
    title = `${longestBreadcrumb.title} | ${title}`;
  }
  return title;
}
