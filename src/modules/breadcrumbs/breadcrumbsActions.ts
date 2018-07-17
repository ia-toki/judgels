import { PopBreadcrumb, PushBreadcrumb } from './breadcrumbsReducer';

export const breadcrumbsActions = {
  push: (link: string, title: string) => PushBreadcrumb.create({ link, title }),
  pop: (link: string) => PopBreadcrumb.create({ link }),
};
