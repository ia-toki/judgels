import {
  Book,
  Box,
  Cog,
  Console,
  Learning,
  Manual,
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
import ProblemTagFilter from '../../components/ProblemTagFilter/ProblemTagFilter';
import {
  isContestAdmin,
  isProblemAdmin,
  isProblemWriter,
  isSystemAdmin,
  isTrainingAdmin,
} from '../../modules/api/userRole';
import { problemAllTagsQueryOptions } from '../../modules/queries/problemTag';
import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';
import { isRatingEnabled, isTrainingEnabled } from '../../modules/webConfig';

const AdminProblemTagFilter = () => <ProblemTagFilter queryOptions={problemAllTagsQueryOptions()} />;

export default function AdminLayout() {
  const {
    data: { roles },
  } = useSuspenseQuery(userWebConfigQueryOptions());

  const canAdministerSystem = isSystemAdmin(roles);
  const canWriteProblems = isProblemWriter(roles);
  const canManageRoles = canAdministerSystem || isProblemAdmin(roles);
  const canAdministerContests = isContestAdmin(roles);
  const canAdministerTraining = isTrainingEnabled() && isTrainingAdmin(roles);

  const sidebarItems = [
    {
      title: 'System',
      visible: canManageRoles,
      children: [
        {
          path: 'users',
          titleIcon: <People />,
          title: 'Users',
          visible: canAdministerSystem,
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
          visible: canAdministerSystem && isRatingEnabled(),
        },
        {
          path: 'settings',
          titleIcon: <Cog />,
          title: 'Settings',
          visible: canAdministerSystem,
        },
      ].filter(child => child.visible !== false),
    },
    {
      title: 'Writing',
      visible: canWriteProblems || canAdministerContests,
      children: [
        {
          path: 'problems',
          titleIcon: <Manual />,
          title: 'Problems',
          visible: canWriteProblems,
          widgetComponent: isProblemAdmin(roles) ? AdminProblemTagFilter : undefined,
          widgetExact: true,
        },
        {
          path: 'contests',
          titleIcon: <Console />,
          title: 'Contests',
          visible: canAdministerContests,
        },
      ].filter(child => child.visible !== false),
    },
    {
      title: 'Training',
      visible: canAdministerTraining,
      children: [
        {
          path: 'curriculums',
          titleIcon: <Learning />,
          title: 'Curriculum',
        },
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
          path: 'lessons',
          titleIcon: <Book />,
          title: 'Lessons',
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
