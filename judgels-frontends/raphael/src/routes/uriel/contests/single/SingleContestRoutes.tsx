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
import { ButtonLink } from 'components/ButtonLink/ButtonLink';
import { Contest } from 'modules/api/uriel/contest';
import { ContestTab, ContestWebConfig } from 'modules/api/uriel/contestWeb';
import { AppState } from 'modules/store';

import ContestStateWidget from './components/ContestStateWidget/ContestStateWidget';
import { LoadingContestStateWidget } from './components/ContestStateWidget/LoadingContestStateWidget';
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

interface SingleContestRoutesProps extends RouteComponentProps<{ contestSlug: string }> {
  contest?: Contest;
  contestWebConfig?: ContestWebConfig;
}

const SingleContestRoutes = (props: SingleContestRoutesProps) => {
  const { contest, contestWebConfig } = props;

  // Optimization:
  // We wait until we get the contest from the backend only if the current slug is different from the persisted one.
  if (!contest || contest.slug !== props.match.params.contestSlug) {
    return <LoadingState large />;
  }

  const visibleTabs = contestWebConfig && contestWebConfig.visibleTabs;
  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: '@',
      titleIcon: 'properties',
      title: 'Overview',
      routeComponent: Route,
      component: ContestOverviewPage,
    },
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
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Announcements) === -1,
    },
    {
      id: 'problems',
      titleIcon: 'manual',
      title: 'Problems',
      routeComponent: Route,
      component: ContestProblemRoutes,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Problems) === -1,
    },
    {
      id: 'submissions',
      titleIcon: 'layers',
      title: 'Submissions',
      routeComponent: Route,
      component: ContestSubmissionRoutes,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Submissions) === -1,
    },
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
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Clarifications) === -1,
    },
    {
      id: 'scoreboard',
      titleIcon: 'th',
      title: 'Scoreboard',
      routeComponent: Route,
      component: ContestScoreboardPage,
      disabled: !visibleTabs || visibleTabs.indexOf(ContestTab.Scoreboard) === -1,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Contest Menu',
    action: (
      <ButtonLink to="/contests" className="bp3-small bp3-icon-chevron-left">
        Back
      </ButtonLink>
    ),
    items: sidebarItems,
    contentHeader: (
      <div className="single-contest-routes__header">
        <h2>{contest && contest.name}</h2>
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
      contestWebConfig: selectContestWebConfig(state),
    } as Partial<SingleContestRoutesProps>);
  return withRouter<any>(connect(mapStateToProps)(SingleContestRoutes));
}

export default createSingleContestRoutes();
