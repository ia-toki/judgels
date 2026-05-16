import {
  Box,
  Console,
  PanelStats,
  People,
  PredictiveAnalysis,
  Properties,
  Shield,
  TimelineLineChart,
} from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet } from '@tanstack/react-router';

import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullWidthPageLayout } from '../../components/FullWidthPageLayout/FullWidthPageLayout';
import { isTLX } from '../../conf';
import { ContestAdminRole } from '../../modules/api/contestAdminRole';
import { TrainingAdminRole } from '../../modules/api/trainingAdminRole';
import { UserAdminRole } from '../../modules/api/userAdminRole';
import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';

export default function AdminLayout() {
  const {
    data: { role },
  } = useSuspenseQuery(userWebConfigQueryOptions());

  const isJophielAdmin = role.jophiel === UserAdminRole.Admin || role.jophiel === UserAdminRole.Superadmin;
  const isUrielAdmin = role.uriel === ContestAdminRole.Admin;
  const isJerahmeelAdmin = isTLX() && role.jerahmeel === TrainingAdminRole.Admin;

  const sidebarItems = [
    {
      path: 'users',
      titleIcon: <People />,
      title: 'Users',
      visible: isJophielAdmin,
    },
    {
      path: 'roles',
      titleIcon: <Shield />,
      title: 'Roles',
      visible: isJophielAdmin,
    },
    {
      path: 'ratings',
      titleIcon: <TimelineLineChart />,
      title: 'Ratings',
      visible: isTLX() && isJophielAdmin,
    },
    {
      path: 'contests',
      titleIcon: <Console />,
      title: 'Contests',
      visible: isUrielAdmin,
    },
    {
      path: 'courses',
      titleIcon: <PredictiveAnalysis />,
      title: 'Courses',
      visible: isJerahmeelAdmin,
    },
    {
      path: 'chapters',
      titleIcon: <Properties />,
      title: 'Chapters',
      visible: isJerahmeelAdmin,
    },
    {
      path: 'archives',
      titleIcon: <Box />,
      title: 'Archives',
      visible: isJerahmeelAdmin,
    },
    {
      path: 'problemsets',
      titleIcon: <PanelStats />,
      title: 'Problemsets',
      visible: isJerahmeelAdmin,
    },
  ].filter(item => item.visible);

  const contentWithSidebarProps = {
    title: 'Admin',
    items: sidebarItems,
    basePath: '/admin',
  };

  return (
    <FullWidthPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullWidthPageLayout>
  );
}
