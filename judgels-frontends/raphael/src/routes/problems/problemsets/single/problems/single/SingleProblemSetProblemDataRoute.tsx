import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { AppState } from '../../../../../../modules/store';
import { ProblemInfo } from '../../../../../../modules/api/sandalphon/problem';
import { ProblemSet } from '../../../../../../modules/api/jerahmeel/problemSet';
import { selectProblemSet } from '../../../modules/problemSetSelectors';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../modules/breadcrumbs/breadcrumbsActions';
import { problemSetProblemActions as injectedProblemSetProblemActions } from '../modules/problemSetProblemActions';

export interface SingleProblemSetProblemDataRouteProps extends RouteComponentProps<{ problemAlias: string }> {
  problemSet?: ProblemSet;

  onClearProblem: () => void;
  onGetProblem: (problemSetJid: string, problemAlias: string) => Promise<ProblemInfo>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

class SingleProblemSetProblemDataRoute extends React.Component<SingleProblemSetProblemDataRouteProps> {
  async componentDidMount() {
    await this.refresh();
  }

  async componentDidUpdate(prevProps: SingleProblemSetProblemDataRouteProps) {
    if ((prevProps.problemSet && prevProps.problemSet.jid) !== (this.props.problemSet && this.props.problemSet.jid)) {
      await this.refresh();
    }
  }

  componentWillUnmount() {
    this.props.onClearProblem();
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    return null;
  }

  refresh = async () => {
    if (!this.props.problemSet) {
      return;
    }
    await this.props.onGetProblem(this.props.problemSet.jid, this.props.match.params.problemAlias);
    this.props.onPushBreadcrumb(this.props.match.url, this.props.match.params.problemAlias);
  };
}

export function createSingleProblemSetProblemDataRoute(problemSetProblemActions, breadcrumbsActions) {
  const mapStateToProps = (state: AppState) =>
    ({
      problemSet: selectProblemSet(state),
    } as Partial<SingleProblemSetProblemDataRouteProps>);

  const mapDispatchToProps = {
    onGetProblem: problemSetProblemActions.getProblem,
    onClearProblem: problemSetProblemActions.clearProblem,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(SingleProblemSetProblemDataRoute));
}

export default createSingleProblemSetProblemDataRoute(injectedProblemSetProblemActions, injectedBreadcrumbsActions);
