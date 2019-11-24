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
import ProblemStatementPage from './statement/ProblemStatementPage/ProblemStatementPage';
import { AppState } from '../../../../../../modules/store';
import { ProblemSet } from '../../../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetProblem } from '../../../../../../modules/api/jerahmeel/problemSetProblem';
import { selectProblemSet } from '../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../modules/problemSetProblemSelectors';

interface SingleProblemSetProblemRoutesProps extends RouteComponentProps<{ problemAlias: string }> {
  problemSet?: ProblemSet;
  problemSetProblem?: ProblemSetProblem;
  onClickBack: (problemSetSlug: string) => void;
}

class SingleProblemSetProblemRoutes extends React.Component<SingleProblemSetProblemRoutesProps> {
  render() {
    const { problemSetProblem } = this.props;

    // Optimization:
    // We wait until we get the problem from the backend only if the current alias is different from the persisted one.
    if (!problemSetProblem || problemSetProblem.alias !== this.props.match.params.problemAlias) {
      return <LoadingState large />;
    }

    const sidebarItems: ContentWithSidebarItem[] = [
      {
        id: '@',
        titleIcon: 'panel-stats',
        title: 'Statement',
        routeComponent: Route,
        component: ProblemStatementPage,
      },
    ];

    const contentWithSidebarProps: ContentWithSidebarProps = {
      title: 'Problem Menu',
      items: sidebarItems,
      action: (
        <Button small icon="chevron-left" onClick={this.clickBack}>
          Back
        </Button>
      ),
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
    problemSetProblem: selectProblemSetProblem(state),
  });

  const mapDispatchToProps = {
    onClickBack: problemSetSlug => push(`/problems/${problemSetSlug}`),
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(SingleProblemSetProblemRoutes));
}

export default createSingleProblemSetProblemRoutes();
