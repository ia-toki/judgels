import { PopBreadcrumb, PushBreadcrumb } from './breadcrumbsReducer';

export const breadcrumbsActions = {
  pushBreadcrumb: (link: string, title: string) => PushBreadcrumb.create({ link, title }),
  popBreadcrumb: (link: string) => PopBreadcrumb.create({ link }),
};
