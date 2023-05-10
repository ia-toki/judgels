import { PopBreadcrumb, PushBreadcrumb } from './breadcrumbsReducer';

export const pushBreadcrumb = (link, title) => PushBreadcrumb({ link, title });

export const popBreadcrumb = link => PopBreadcrumb({ link });

export const breadcrumbsActions = {
  pushBreadcrumb,
  popBreadcrumb,
};
