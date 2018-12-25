import * as React from 'react';
import { connect } from 'react-redux';
import { Route, RouteComponentProps, withRouter } from 'react-router';

import { FullPageLayout } from 'components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from 'components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from 'components/ContentWithSidebar/ContentWithSidebar';
import { LoadingState } from 'components/LoadingState/LoadingState';
import { ContestRoleTag } from 'components/ContestRole/ContestRoleTag';
import { Contest } from 'modules/api/uriel/contest';
import { ContestTab, ContestWebConfig } from 'modules/api/uriel/contestWeb';
import { AppState } from 'modules/store';

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
import ContestScoreboardPage from './scoreboard/ContestScoreboardPage/ContestScoreboardPage';
import ContestFilesPage from './files/ContestFilesPage/ContestFilesPage';
import ContestSubmissionRoutes from './submissions/ContestSubmissionRoutes';
import { EditContest } from '../modules/contestReducer';
import { selectContest, selectIsEditingContest } from '../modules/contestSelectors';
import { selectContestWebConfig } from '../modules/contestWebConfigSelectors';
import { contestIcon } from './modules/contestIcon';

import './SingleContestRoutes.css';

interface SingleContestRoutesProps extends RouteComponentProps<{ contestSlug: string }> {
  contest?: Contest;
  isEditingContest: boolean;
  contestWebConfig?: ContestWebConfig;
  onSetNotEditingContest: () => any;
}

const SingleContestRoutes = (props: SingleContestRoutesProps) => {
  const { contest, isEditingContest, contestWebConfig, onSetNotEditingContest } = props;

  // Optimization:
  // We wait until we get the contest from the backend only if the current slug is different from the persisted one.
  if (!contest || contest.slug !== props.match.params.contestSlug) {
    return <LoadingState large />;
  }

  const visibleTabs = contestWebConfig && contestWebConfig.visibleTabs;
  const sidebarItems: ContentWithSidebarItem[] = [
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
          <div className="tab-item-with-widget__name">Announcements</div>
          <div className="tab-item-with-widget__widget">
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
      component: ContestSubmissionRoutes,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Submissions) === -1,
    },
    {
      id: 'clarifications',
      titleIcon: contestIcon[ContestTab.Clarifications],
      title: (
        <div className="tab-item-with-widget">
          <div className="tab-item-with-widget__name">Clarifications</div>
          <div className="tab-item-with-widget__widget">
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
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Contest Menu',
    action: contestWebConfig && <ContestRoleTag role={contestWebConfig.role} />,
    items: sidebarItems,
    contentHeader: (
      <div className="single-contest-routes__header">
        <h2 className="single-contest-routes__title">{contest.name}</h2>
        {contestWebConfig && (
          <div className="single-contest-routes__button">
            <ContestEditDialog
              contest={contest!}
              canManage={contestWebConfig!.canManage}
              isEditingContest={isEditingContest}
              onSetNotEditingContest={onSetNotEditingContest}
            />
          </div>
        )}
        <div className="clearfix" />
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
};

function createSingleContestRoutes() {
  const mapStateToProps = (state: AppState) =>
    ({
      contest: selectContest(state),
      isEditingContest: selectIsEditingContest(state),
      contestWebConfig: selectContestWebConfig(state),
    } as Partial<SingleContestRoutesProps>);

  const mapDispatchToProps = {
    onSetNotEditingContest: () => EditContest.create(false),
  };
  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(SingleContestRoutes));
}

export default createSingleContestRoutes();
