import { Intent, Tag } from '@blueprintjs/core';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ContestRoleTag } from '../../../../components/ContestRole/ContestRoleTag';
import { ContestStyle } from '../../../../modules/api/uriel/contest';
import { ContestTab } from '../../../../modules/api/uriel/contestWeb';

import { ContestEditDialog } from './components/ContestEditDialog/ContestEditDialog';
import ContestStateWidget from './components/ContestStateWidget/ContestStateWidget';
import { LoadingContestStateWidget } from './components/ContestStateWidget/LoadingContestStateWidget';
import ContestAnnouncementsWidget from './components/ContestAnnouncementsWidget/ContestAnnouncementsWidget';
import ContestClarificationsWidget from './components/ContestClarificationsWidget/ContestClarificationsWidget';
import ContestContestantsPage from './contestants/ContestContestantsPage/ContestContestantsPage';
import ContestSupervisorsPage from './supervisors/ContestSupervisorsPage/ContestSupervisorsPage';
import ContestManagersPage from './managers/ContestManagersPage/ContestManagersPage';
import ContestOverviewPage from './overview/ContestOverviewPage/ContestOverviewPage';
import ContestAnnouncementsPage from './announcements/ContestAnnouncementsPage/ContestAnnouncementsPage';
import ContestClarificationsPage from './clarifications/ContestClarificationsPage/ContestClarificationsPage';
import ContestProblemRoutes from './problems/ContestProblemRoutes';
import ContestEditorialPage from './editorial/ContestEditorialPage/ContestEditorialPage';
import ContestScoreboardPage from './scoreboard/ContestScoreboardPage/ContestScoreboardPage';
import ContestFilesPage from './files/ContestFilesPage/ContestFilesPage';
import ContestLogsPage from './logs/ContestLogsPage/ContestLogsPage';
import ProgrammingSubmissionRoutes from './submissions/Programming/ContestSubmissionRoutes';
import BundleSubmissionRoutes from './submissions/Bundle/ContestSubmissionRoutes';
import { EditContest } from '../modules/contestReducer';
import { selectContest, selectIsEditingContest } from '../modules/contestSelectors';
import { selectContestWebConfig } from '../modules/contestWebConfigSelectors';
import { contestIcon } from './modules/contestIcon';

import './SingleContestRoutes.scss';

function SingleContestRoutes({ match, contest, isEditingContest, contestWebConfig, onSetNotEditingContest }) {
  // Optimization:
  // We wait until we get the contest from the backend only if the current slug is different from the persisted one.
  if (!contest || contest.slug !== match.params.contestSlug) {
    return <LoadingState large />;
  }

  const visibleTabs = contestWebConfig && contestWebConfig.visibleTabs;
  const sidebarItems = [
    {
      id: '@',
      titleIcon: contestIcon[ContestTab.Overview],
      title: 'Overview',
      routeComponent: Route,
      component: ContestOverviewPage,
    },
    {
      id: 'announcements',
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
      routeComponent: Route,
      component: ContestAnnouncementsPage,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Announcements) === -1,
    },
    {
      id: 'problems',
      titleIcon: contestIcon[ContestTab.Problems],
      title: 'Problems',
      routeComponent: Route,
      component: ContestProblemRoutes,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Problems) === -1,
    },
    {
      id: 'editorial',
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
      routeComponent: Route,
      component: ContestEditorialPage,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Editorial) === -1,
    },
    {
      id: 'contestants',
      titleIcon: contestIcon[ContestTab.Contestants],
      title: 'Contestants',
      routeComponent: Route,
      component: ContestContestantsPage,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Contestants) === -1,
    },
    {
      id: 'supervisors',
      titleIcon: contestIcon[ContestTab.Supervisors],
      title: 'Supervisors',
      routeComponent: Route,
      component: ContestSupervisorsPage,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Supervisors) === -1,
    },
    {
      id: 'managers',
      titleIcon: contestIcon[ContestTab.Managers],
      title: 'Managers',
      routeComponent: Route,
      component: ContestManagersPage,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Managers) === -1,
    },
    {
      id: 'submissions',
      titleIcon: contestIcon[ContestTab.Submissions],
      title: 'Submissions',
      routeComponent: Route,
      component: contest.style === ContestStyle.Bundle ? BundleSubmissionRoutes : ProgrammingSubmissionRoutes,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Submissions) === -1,
    },
    {
      id: 'clarifications',
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
      routeComponent: Route,
      component: ContestClarificationsPage,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Clarifications) === -1,
    },
    {
      id: 'scoreboard',
      titleIcon: contestIcon[ContestTab.Scoreboard],
      title: 'Scoreboard',
      routeComponent: Route,
      component: ContestScoreboardPage,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Scoreboard) === -1,
    },
    {
      id: 'files',
      titleIcon: contestIcon[ContestTab.Files],
      title: 'Files',
      routeComponent: Route,
      component: ContestFilesPage,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Files) === -1,
    },
    {
      id: 'logs',
      titleIcon: contestIcon[ContestTab.Logs],
      title: 'Logs',
      routeComponent: Route,
      component: ContestLogsPage,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Logs) === -1,
    },
  ];

  const contentWithSidebarProps = {
    title: 'Contest Menu',
    action: contestWebConfig && <ContestRoleTag role={contestWebConfig.role} />,
    items: sidebarItems,
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
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
}

const mapStateToProps = state => ({
  contest: selectContest(state),
  isEditingContest: selectIsEditingContest(state),
  contestWebConfig: selectContestWebConfig(state),
});

const mapDispatchToProps = {
  onSetNotEditingContest: () => EditContest(false),
};
export default withRouter(connect(mapStateToProps, mapDispatchToProps)(SingleContestRoutes));
