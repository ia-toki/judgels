import { Component } from 'react';
import { withRouter } from 'react-router';
import { connect } from 'react-redux';

import { selectProblemSet } from '../modules/problemSetSelectors';
import * as problemSetActions from '../modules/problemSetActions';
import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';

class SingleProblemSetDataRoute extends Component {
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

const mapStateToProps = state => ({
  problemSet: selectProblemSet(state),
});

const mapDispatchToProps = {
  onGetProblemSetBySlug: problemSetActions.getProblemSetBySlug,
  onClearProblemSet: problemSetActions.clearProblemSet,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(SingleProblemSetDataRoute));
