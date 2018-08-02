import * as React from 'react';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from 'components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from 'components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from 'components/ContentWithSidebar/ContentWithSidebar';
import { LoadingState } from 'components/LoadingState/LoadingState';
import { ButtonLink } from 'components/ButtonLink/ButtonLink';
import { Contest } from 'modules/api/uriel/contest';
import { ContestTab, ContestWebConfig } from 'modules/api/uriel/contestWeb';
import { AppState } from 'modules/store';

import ContestStateWidget from './components/ContestStateWidget/ContestStateWidget';
import ContestAnnouncementsWidget from './components/ContestAnnouncementsWidget/ContestAnnouncementsWidget';
import ContestClarificationsWidget from './components/ContestClarificationsWidget/ContestClarificationsWidget';
import ContestOverviewPage from './overview/ContestOverviewPage/ContestOverviewPage';
import ContestAnnouncementsPage from './announcements/ContestAnnouncementsPage/ContestAnnouncementsPage';
import ContestClarificationsPage from './clarifications/ContestClarificationsPage/ContestClarificationsPage';
import ContestProblemRoutes from './problems/ContestProblemRoutes';
import ContestScoreboardPage from './scoreboard/ContestScoreboardPage/ContestScoreboardPage';
import ContestSubmissionRoutes from './submissions/ContestSubmissionRoutes';
import { selectContest } from '../modules/contestSelectors';
import { selectContestWebConfig } from '../modules/contestWebConfigSelectors';

import './SingleContestRoutes.css';

interface SingleContestRoutesProps {
  contest?: Contest;
  contestWebConfig?: ContestWebConfig;
}

const SingleContestRoutes = (props: SingleContestRoutesProps) => {
  const { contest, contestWebConfig } = props;
  if (!contest || !contestWebConfig) {
    return <LoadingState large />;
  }

  let sidebarItems: ContentWithSidebarItem[] = [
    {
      id: '@',
      titleIcon: 'properties',
      title: 'Overview',
      routeComponent: Route,
      component: ContestOverviewPage,
    },
  ];

  const visibleTabs = contestWebConfig!.visibleTabs;
  if (visibleTabs.indexOf(ContestTab.Announcements) !== -1) {
    sidebarItems = [
      ...sidebarItems,
      {
        id: 'announcements',
        titleIcon: 'notifications',
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
      },
    ];
  }
  if (visibleTabs.indexOf(ContestTab.Problems) !== -1) {
    sidebarItems = [
      ...sidebarItems,
      {
        id: 'problems',
        titleIcon: 'manual',
        title: 'Problems',
        routeComponent: Route,
        component: ContestProblemRoutes,
      },
    ];
  }
  if (visibleTabs.indexOf(ContestTab.Submissions) !== -1) {
    sidebarItems = [
      ...sidebarItems,
      {
        id: 'submissions',
        titleIcon: 'layers',
        title: 'Submissions',
        routeComponent: Route,
        component: ContestSubmissionRoutes,
      },
    ];
  }
  if (visibleTabs.indexOf(ContestTab.Clarifications) !== -1) {
    sidebarItems = [
      ...sidebarItems,
      {
        id: 'clarifications',
        titleIcon: 'chat',
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
      },
    ];
  }
  if (visibleTabs.indexOf(ContestTab.Scoreboard) !== -1) {
    sidebarItems = [
      ...sidebarItems,
      {
        id: 'scoreboard',
        titleIcon: 'th',
        title: 'Scoreboard',
        routeComponent: Route,
        component: ContestScoreboardPage,
      },
    ];
  }

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Contest Menu',
    action: (
      <ButtonLink to="/contests" className="pt-small pt-icon-chevron-left">
        Back
      </ButtonLink>
    ),
    items: sidebarItems,
    contentHeader: (
      <div className="single-contest-routes__header">
        <h2>{contest.name}</h2>
        <ContestStateWidget />
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
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state),
    contestWebConfig: selectContestWebConfig(state),
  });
  return withRouter<any>(connect(mapStateToProps)(SingleContestRoutes));
}

export default createSingleContestRoutes();
