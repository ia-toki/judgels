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
import { isTLX } from '../../conf';
import {
  isContestAdmin,
  isProblemAdmin,
  isProblemWriter,
  isSystemAdmin,
  isTrainingAdmin,
} from '../../modules/api/userRole';
import { problemAllTagsQueryOptions } from '../../modules/queries/problemTag';
import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';

const AdminProblemTagFilter = () => <ProblemTagFilter queryOptions={problemAllTagsQueryOptions()} />;

export default function AdminLayout() {
  const {
    data: { roles },
  } = useSuspenseQuery(userWebConfigQueryOptions());

  const canAdministerSystem = isSystemAdmin(roles);
  const canWriteProblems = isProblemWriter(roles);
  const canAdministerContests = isContestAdmin(roles);
  const canAdministerTraining = isTLX() && isTrainingAdmin(roles);

  const sidebarItems = [
    {
      title: 'System',
      visible: canAdministerSystem,
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
          path: 'settings',
          titleIcon: <Cog />,
          title: 'Settings',
        },
      ].filter(child => child.visible !== false),
    },
    {
      title: 'Competition',
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
