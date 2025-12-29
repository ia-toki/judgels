import { Intent, Tag } from '@blueprintjs/core';
import { useDispatch, useSelector } from 'react-redux';
import { Outlet, useParams } from 'react-router';

import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { ContestRoleTag } from '../../../../components/ContestRole/ContestRoleTag';
import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { ContestTab } from '../../../../modules/api/uriel/contestWeb';
import { EditContest } from '../modules/contestReducer';
import { selectContest, selectIsEditingContest } from '../modules/contestSelectors';
import { selectContestWebConfig } from '../modules/contestWebConfigSelectors';
import SingleContestDataLayout from './SingleContestDataLayout';
import ContestAnnouncementsPage from './announcements/ContestAnnouncementsPage/ContestAnnouncementsPage';
import ContestClarificationsPage from './clarifications/ContestClarificationsPage/ContestClarificationsPage';
import ContestAnnouncementsWidget from './components/ContestAnnouncementsWidget/ContestAnnouncementsWidget';
import ContestClarificationsWidget from './components/ContestClarificationsWidget/ContestClarificationsWidget';
import { ContestEditDialog } from './components/ContestEditDialog/ContestEditDialog';
import ContestStateWidget from './components/ContestStateWidget/ContestStateWidget';
import { LoadingContestStateWidget } from './components/ContestStateWidget/LoadingContestStateWidget';
import ContestContestantsPage from './contestants/ContestContestantsPage/ContestContestantsPage';
import ContestEditorialPage from './editorial/ContestEditorialPage/ContestEditorialPage';
import ContestFilesPage from './files/ContestFilesPage/ContestFilesPage';
import ContestLogsPage from './logs/ContestLogsPage/ContestLogsPage';
import ContestManagersPage from './managers/ContestManagersPage/ContestManagersPage';
import { contestIcon } from './modules/contestIcon';
import ContestOverviewPage from './overview/ContestOverviewPage/ContestOverviewPage';
import ContestProblemsPage from './problems/ContestProblemsPage/ContestProblemsPage';
import ContestProblemPage from './problems/single/ContestProblemPage/ContestProblemPage';
import ContestScoreboardPage from './scoreboard/ContestScoreboardPage/ContestScoreboardPage';
import { ContestSubmissionLayout, contestSubmissionRoutes } from './submissions/ContestSubmissionRoutes';
import ContestSupervisorsPage from './supervisors/ContestSupervisorsPage/ContestSupervisorsPage';

import './SingleContestRoutes.scss';

export const singleContestRoutes = [
  {
    index: true,
    element: <ContestOverviewPage />,
  },
  {
    path: 'announcements',
    element: <ContestAnnouncementsPage />,
  },
  {
    path: 'problems',
    element: <ContestProblemsPage />,
  },
  {
    path: 'problems/:problemAlias',
    element: <ContestProblemPage />,
  },
  {
    path: 'editorial',
    element: <ContestEditorialPage />,
  },
  {
    path: 'contestants',
    element: <ContestContestantsPage />,
  },
  {
    path: 'supervisors',
    element: <ContestSupervisorsPage />,
  },
  {
    path: 'managers',
    element: <ContestManagersPage />,
  },
  {
    path: 'submissions',
    element: <ContestSubmissionLayout />,
    children: contestSubmissionRoutes,
  },
  {
    path: 'clarifications',
    element: <ContestClarificationsPage />,
  },
  {
    path: 'scoreboard',
    element: <ContestScoreboardPage />,
  },
  {
    path: 'files',
    element: <ContestFilesPage />,
  },
  {
    path: 'logs',
    element: <ContestLogsPage />,
  },
];

export function SingleContestLayout() {
  return (
    <>
      <SingleContestDataLayout />
      <MainSingleContestLayout />
    </>
  );
}

function MainSingleContestLayout() {
  const { contestSlug } = useParams();
  const dispatch = useDispatch();
  const contest = useSelector(selectContest);
  const isEditingContest = useSelector(selectIsEditingContest);
  const contestWebConfig = useSelector(selectContestWebConfig);

  const onSetNotEditingContest = () => dispatch(EditContest(false));

  // Optimization:
  // We wait until we get the contest from the backend only if the current slug is different from the persisted one.
  if (!contest || contest.slug !== contestSlug) {
    return <LoadingState large />;
  }

  const visibleTabs = contestWebConfig && contestWebConfig.visibleTabs;
  const sidebarItems = [
    {
      path: '',
      titleIcon: contestIcon[ContestTab.Overview],
      title: 'Overview',
    },
    {
      path: 'announcements',
      titleIcon: contestIcon[ContestTab.Announcements],
      title: (
        <div className="tab-item-with-widget">
          <div className="float-left">Announcements</div>
          <div className="float-right">
            <ContestAnnouncementsWidget />
          </div>
          <div className="clearfix" />
        </div>
      ),
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Announcements) === -1,
    },
    {
      path: 'problems',
      titleIcon: contestIcon[ContestTab.Problems],
      title: 'Problems',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Problems) === -1,
    },
    {
      path: 'editorial',
      titleIcon: contestIcon[ContestTab.Editorial],
      title: (
        <div className="tab-item-with-widget">
          <div className="float-left">Editorial</div>
          <div className="float-right">
            <Tag className="normal-weight" intent={Intent.WARNING}>
              NEW
            </Tag>
          </div>
          <div className="clearfix" />
        </div>
      ),
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Editorial) === -1,
    },
    {
      path: 'contestants',
      titleIcon: contestIcon[ContestTab.Contestants],
      title: 'Contestants',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Contestants) === -1,
    },
    {
      path: 'supervisors',
      titleIcon: contestIcon[ContestTab.Supervisors],
      title: 'Supervisors',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Supervisors) === -1,
    },
    {
      path: 'managers',
      titleIcon: contestIcon[ContestTab.Managers],
      title: 'Managers',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Managers) === -1,
    },
    {
      path: 'submissions',
      titleIcon: contestIcon[ContestTab.Submissions],
      title: 'Submissions',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Submissions) === -1,
    },
    {
      path: 'clarifications',
      titleIcon: contestIcon[ContestTab.Clarifications],
      title: (
        <div className="tab-item-with-widget">
          <div className="float-left">Clarifications</div>
          <div className="float-right">
            <ContestClarificationsWidget />
          </div>
          <div className="clearfix" />
        </div>
      ),
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Clarifications) === -1,
    },
    {
      path: 'scoreboard',
      titleIcon: contestIcon[ContestTab.Scoreboard],
      title: 'Scoreboard',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Scoreboard) === -1,
    },
    {
      path: 'files',
      titleIcon: contestIcon[ContestTab.Files],
      title: 'Files',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Files) === -1,
    },
    {
      path: 'logs',
      titleIcon: contestIcon[ContestTab.Logs],
      title: 'Logs',
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Logs) === -1,
    },
  ];

  const contentWithSidebarProps = {
    title: 'Contest Menu',
    action: contestWebConfig && <ContestRoleTag role={contestWebConfig.role} />,
    items: sidebarItems,
    basePath: `/contests/${contestSlug}`,
    contentHeader: (
      <div className="single-contest-routes__header">
        <div className="single-contest-routes__heading">
          <h2>{contest.name}</h2>
          {contestWebConfig && (
            <div className="single-contest-routes__action">
              <ContestEditDialog
                contest={contest}
                canManage={contestWebConfig.canManage}
                isEditingContest={isEditingContest}
                onSetNotEditingContest={onSetNotEditingContest}
              />
            </div>
          )}
        </div>
        {contestWebConfig ? <ContestStateWidget /> : <LoadingContestStateWidget />}
      </div>
    ),
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullPageLayout>
  );
}
