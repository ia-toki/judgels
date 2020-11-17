import { PopBreadcrumb, PushBreadcrumb } from './breadcrumbsReducer';

export const pushBreadcrumb = (link: string, title: string) => PushBreadcrumb.create({ link, title });

export const popBreadcrumb = (link: string) => PopBreadcrumb.create({ link });

export const breadcrumbsActions = {
  pushBreadcrumb,
  popBreadcrumb,
};
