import { Outlet } from '@tanstack/react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

function CoursesLayout() {
  return <Outlet />;
}

export default withBreadcrumb('Courses')(CoursesLayout);
