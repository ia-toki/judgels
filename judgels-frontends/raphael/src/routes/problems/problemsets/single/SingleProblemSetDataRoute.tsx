import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { AppState } from '../../../../modules/store';
import { ProblemSet } from '../../../../modules/api/jerahmeel/problemSet';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../modules/breadcrumbs/breadcrumbsActions';

import { selectProblemSet } from '../modules/problemSetSelectors';
import { problemSetActions as injectedProblemSetActions } from '../modules/problemSetActions';

export interface SingleProblemSetDataRouteProps extends RouteComponentProps<{ problemSetSlug: string }> {
  problemSet?: ProblemSet;

  onClearProblemSet: () => void;
  onGetProblemSetBySlug: (problemSetJid: string) => Promise<ProblemSet>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

class SingleProblemSetDataRoute extends React.Component<SingleProblemSetDataRouteProps> {
  async componentDidMount() {
    const problemSet = await this.props.onGetProblemSetBySlug(this.props.match.params.problemSetSlug);
    this.props.onPushBreadcrumb(this.props.match.url, problemSet.name);
  }

  componentWillUnmount() {
    this.props.onClearProblemSet();
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    return null;
  }
}

export function createSingleProblemSetDataRoute(problemSetActions, breadcrumbsActions) {
  const mapStateToProps = (state: AppState) =>
    ({
      problemSet: selectProblemSet(state),
    } as Partial<SingleProblemSetDataRouteProps>);

  const mapDispatchToProps = {
    onGetProblemSetBySlug: problemSetActions.getProblemSetBySlug,
    onClearProblemSet: problemSetActions.clearProblemSet,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(SingleProblemSetDataRoute));
}

export default createSingleProblemSetDataRoute(injectedProblemSetActions, injectedBreadcrumbsActions);
