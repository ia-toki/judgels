import * as React from 'react';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../../../../../../../components/layouts/FullPageLayout/FullPageLayout';

import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../../../../../../components/ContentWithSidebar/ContentWithSidebar';
import ContestScoreboardPage from './scoreboard/ContestScoreboardPage/ContestScoreboardPage';
import { Contest } from '../../../../../../../../modules/api/uriel/contest';
import { AppState } from '../../../../../../../../modules/store';
import { selectContest } from '../../../../../modules/contestSelectors';

interface SingleContestRoutesProps {
  contest?: Contest;
}

const SingleContestRoutes = (props: SingleContestRoutesProps) => {
  if (!props.contest) {
    return null;
  }

  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: 'scoreboard',
      title: 'Scoreboard',
      routeComponent: Route,
      component: ContestScoreboardPage,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Menu',
    items: sidebarItems,
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
