import {
  Box,
  Cog,
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

  const isAccountAdmin = role.account === UserAdminRole.Admin || role.account === UserAdminRole.Superadmin;
  const isContestAdmin = role.contest === ContestAdminRole.Admin;
  const isTrainingAdmin = isTLX() && role.training === TrainingAdminRole.Admin;

  const sidebarItems = [
    {
      title: 'System',
      visible: isAccountAdmin,
      children: [
        {
          path: 'users',
          titleIcon: <People />,
          title: 'Users',
        },
        {
          path: 'roles',
          titleIcon: <Shield />,
          title: 'Roles',
        },
        {
          path: 'ratings',
          titleIcon: <TimelineLineChart />,
          title: 'Ratings',
          visible: isTLX(),
        },
        {
          path: 'settings/app',
          titleIcon: <Cog />,
          title: 'Settings',
        },
      ].filter(child => child.visible !== false),
    },
    {
      title: 'Contest',
      visible: isContestAdmin,
      children: [
        {
          path: 'contests',
          titleIcon: <Console />,
          title: 'Contests',
        },
      ],
    },
    {
      title: 'Training',
      visible: isTrainingAdmin,
      children: [
        {
          path: 'courses',
          titleIcon: <PredictiveAnalysis />,
          title: 'Courses',
        },
        {
          path: 'chapters',
          titleIcon: <Properties />,
          title: 'Chapters',
        },
        {
          path: 'archives',
          titleIcon: <Box />,
          title: 'Archives',
        },
        {
          path: 'problemsets',
          titleIcon: <PanelStats />,
          title: 'Problemsets',
        },
      ],
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
