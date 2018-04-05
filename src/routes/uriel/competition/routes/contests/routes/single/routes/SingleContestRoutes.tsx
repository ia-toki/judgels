import * as React from 'react';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../../../../../../../components/layouts/FullPageLayout/FullPageLayout';

import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../../../../../../components/ContentWithSidebar/ContentWithSidebar';
import { ButtonLink } from '../../../../../../../../components/ButtonLink/ButtonLink';
import ContestDetailsPage from './details/ContestDetailsPage/ContestDetailsPage';
import ContestScoreboardPage from './scoreboard/ContestScoreboardPage/ContestScoreboardPage';
import { Contest } from '../../../../../../../../modules/api/uriel/contest';
import { AppState } from '../../../../../../../../modules/store';
import { selectContest } from '../../../modules/contestSelectors';

import './SingleContestRoutes.css';

interface SingleContestRoutesProps {
  contest?: Contest;
}

const SingleContestRoutes = (props: SingleContestRoutesProps) => {
  const { contest } = props;
  if (!contest) {
    return null;
  }

  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: 'details',
      titleIcon: 'properties',
      title: 'Details',
      routeComponent: Route,
      component: ContestDetailsPage,
    },
    {
      id: 'scoreboard',
      titleIcon: 'th',
      title: 'Scoreboard',
      routeComponent: Route,
      component: ContestScoreboardPage,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Contest Menu',
    action: (
      <ButtonLink to="/competition/contests" className="pt-small pt-icon-chevron-left">
        Back
      </ButtonLink>
    ),
    items: sidebarItems,
    contentHeader: (
      <div className="single-contest-routes__header">
        <h2>{contest.name}</h2>
        <hr />
      </div>
    ),
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
};

function createSingleContestRoutes() {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state),
  });
  return withRouter<any>(connect(mapStateToProps)(SingleContestRoutes));
}

export default createSingleContestRoutes();
