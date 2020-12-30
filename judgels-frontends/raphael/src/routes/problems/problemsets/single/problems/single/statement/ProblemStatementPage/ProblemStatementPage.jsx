import { Component } from 'react';
import { connect } from 'react-redux';

import { sendGAEvent } from '../../../../../../../../ga';
import { ProblemType } from '../../../../../../../../modules/api/sandalphon/problem';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import ProblemSetProblemProgrammingStatementPage from '../Programming/ProblemStatementPage';
import ProblemSetProblemBundleStatementPage from '../Bundle/ProblemStatementPage';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';
import * as problemSetProblemActions from '../../../modules/problemSetProblemActions';

export class ProblemStatementPage extends Component {
  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetProblemWorksheet(
      this.props.problemSet.jid,
      this.props.problem.alias,
      this.props.statementLanguage
    );

    this.setState({
      response,
    });

    sendGAEvent({ category: 'Problems', action: 'View problemset problem', label: this.props.problemSet.name });
    sendGAEvent({
      category: 'Problems',
      action: 'View problem',
      label: this.props.problemSet.name + ': ' + this.props.problem.alias,
    });
  }

  async componentDidUpdate(prevProps, prevState) {
    if (this.props.statementLanguage !== prevProps.statementLanguage && prevState.response) {
      this.setState({ response: undefined });
    } else if (!this.state.response && prevState.response) {
      await this.componentDidMount();
    }
  }

  render() {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }
    const { problem } = response;
    if (problem.type === ProblemType.Programming) {
      return <ProblemSetProblemProgrammingStatementPage worksheet={response} />;
    } else {
      return <ProblemSetProblemBundleStatementPage worksheet={response} />;
    }
  }
}

const mapStateToProps = state => ({
  problemSet: selectProblemSet(state),
  problem: selectProblemSetProblem(state),
  statementLanguage: selectStatementLanguage(state),
});
const mapDispatchToProps = {
  onGetProblemWorksheet: problemSetProblemActions.getProblemWorksheet,
};
export default connect(mapStateToProps, mapDispatchToProps)(ProblemStatementPage);
