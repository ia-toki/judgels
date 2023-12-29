import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { selectProblemSet } from '../../../modules/problemSetSelectors';

import * as breadcrumbsActions from '../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as problemSetProblemActions from '../modules/problemSetProblemActions';

class SingleProblemSetProblemDataRoute extends Component {
  async componentDidMount() {
    await this.refresh();
  }

  async componentDidUpdate(prevProps) {
    if ((prevProps.problemSet && prevProps.problemSet.jid) !== (this.props.problemSet && this.props.problemSet.jid)) {
      this.props.onPopBreadcrumb(this.props.match.url);
      this.props.onClearProblem();
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
    const { problemSet, match } = this.props;
    if (!problemSet || problemSet.slug !== match.params.problemSetSlug) {
      return;
    }
    await this.props.onGetProblem(problemSet.jid, match.params.problemAlias);
    this.props.onPushBreadcrumb(match.url, match.params.problemAlias);
  };
}

const mapStateToProps = state => ({
  problemSet: selectProblemSet(state),
});

const mapDispatchToProps = {
  onGetProblem: problemSetProblemActions.getProblem,
  onClearProblem: problemSetProblemActions.clearProblem,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(SingleProblemSetProblemDataRoute));
