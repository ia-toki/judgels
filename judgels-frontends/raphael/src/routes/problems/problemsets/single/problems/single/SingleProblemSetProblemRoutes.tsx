import { Button } from '@blueprintjs/core';
import { push } from 'connected-react-router';
import * as React from 'react';
import { connect } from 'react-redux';
import { Route, RouteComponentProps, withRouter } from 'react-router';

import { FullPageLayout } from '../../../../../../components/FullPageLayout/FullPageLayout';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../../../../components/ContentWithSidebar/ContentWithSidebar';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import ProblemStatsWidget from './ProblemStatsWidget/ProblemStatsWidget';
import ProblemStatementPage from './statement/ProblemStatementPage/ProblemStatementPage';
import ProblemSubmissionRoutes from './submissions/ProblemSubmissionRoutes';
import ProblemItemSubmissionRoutes from './results/ProblemItemSubmissionRoutes';
import { AppState } from '../../../../../../modules/store';
import { ProblemType } from '../../../../../../modules/api/sandalphon/problem';
import { ProblemSet } from '../../../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetProblem } from '../../../../../../modules/api/jerahmeel/problemSetProblem';
import { selectProblemSet } from '../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../modules/problemSetProblemSelectors';

import './SingleProblemSetProblemRoutes.css';

interface SingleProblemSetProblemRoutesProps extends RouteComponentProps<{ problemAlias: string }> {
  problemSet?: ProblemSet;
  problem?: ProblemSetProblem;
  onClickBack: (problemSetSlug: string) => void;
}

class SingleProblemSetProblemRoutes extends React.Component<SingleProblemSetProblemRoutesProps> {
  render() {
    const { problemSet, problem } = this.props;

    // Optimization:
    // We wait until we get the problem from the backend only if the current alias is different from the persisted one.
    if (!problem || problem.alias !== this.props.match.params.problemAlias) {
      return <LoadingState large />;
    }

    let sidebarItems: ContentWithSidebarItem[] = [
      {
        id: '@',
        titleIcon: 'document',
        title: 'Statement',
        routeComponent: Route,
        component: ProblemStatementPage,
      },
    ];

    if (problem.type === ProblemType.Programming) {
      sidebarItems = [
        ...sidebarItems,
        {
          id: 'submissions',
          titleIcon: 'layers',
          title: 'Submissions',
          routeComponent: Route,
          component: ProblemSubmissionRoutes,
        },
      ];
    } else {
      sidebarItems = [
        ...sidebarItems,
        {
          id: 'results',
          titleIcon: 'manually-entered-data',
          title: 'Results',
          routeComponent: Route,
          component: ProblemItemSubmissionRoutes,
        },
      ];
    }

    const contentWithSidebarProps: ContentWithSidebarProps = {
      title: 'Problem Menu',
      items: sidebarItems,
      action: (
        <Button small icon="chevron-left" onClick={this.clickBack}>
          Back
        </Button>
      ),
      contentHeader: (
        <h3 className="single-problemset-problem-routes__title">
          {problemSet.name}
          <>&nbsp;&mdash;&nbsp;</>
          Problem {problem.alias}
        </h3>
      ),
      stickyWidget: problem.type === ProblemType.Programming ? ProblemStatsWidget : undefined,
    };

    return (
      <FullPageLayout>
        <ContentWithSidebar {...contentWithSidebarProps} />
      </FullPageLayout>
    );
  }

  private clickBack = () => {
    return this.props.onClickBack(this.props.problemSet.slug);
  };
}

function createSingleProblemSetProblemRoutes() {
  const mapStateToProps = (state: AppState) => ({
    problemSet: selectProblemSet(state),
    problem: selectProblemSetProblem(state),
  });

  const mapDispatchToProps = {
    onClickBack: problemSetSlug => push(`/problems/${problemSetSlug}`),
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(SingleProblemSetProblemRoutes));
}

export default createSingleProblemSetProblemRoutes();
