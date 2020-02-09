import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { AppState } from '../../../../modules/store';
import { ProblemSet } from '../../../../modules/api/jerahmeel/problemSet';
import { selectProblemSet } from '../modules/problemSetSelectors';
import * as problemSetActions from '../modules/problemSetActions';
import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';

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

const mapStateToProps = (state: AppState) => ({
  problemSet: selectProblemSet(state),
});

const mapDispatchToProps = {
  onGetProblemSetBySlug: problemSetActions.getProblemSetBySlug,
  onClearProblemSet: problemSetActions.clearProblemSet,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(SingleProblemSetDataRoute));
