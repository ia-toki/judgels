import * as React from 'react';
import { connect } from 'react-redux';

import { sendGAEvent } from '../../../../../../../../ga';
import { AppState } from '../../../../../../../../modules/store';
import { ProblemType } from '../../../../../../../../modules/api/sandalphon/problem';
import { ProblemSet } from '../../../../../../../../modules/api/jerahmeel/problemSet';
import {
  ProblemSetProblemWorksheet,
  ProblemSetProblem,
} from '../../../../../../../../modules/api/jerahmeel/problemSetProblem';
import { ProblemSetProblemWorksheet as ProblemSetBundleProblemWorksheet } from '../../../../../../../../modules/api/jerahmeel/problemSetProblemBundle';
import { ProblemSetProblemWorksheet as ProblemSetProgrammingProblemWorksheet } from '../../../../../../../../modules/api/jerahmeel/problemSetProblemProgramming';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import ProblemSetProblemProgrammingStatementPage from '../Programming/ProblemStatementPage';
import ProblemSetProblemBundleStatementPage from '../Bundle/ProblemStatementPage';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';
import * as problemSetProblemActions from '../../../modules/problemSetProblemActions';

export interface ProblemStatementPageProps {
  problemSet: ProblemSet;
  problem: ProblemSetProblem;
  statementLanguage: string;
  gradingLanguage: string;
  onGetProblemWorksheet: (
    problemSetJid: string,
    problemAlias: string,
    language?: string
  ) => Promise<ProblemSetProblemWorksheet>;
}

interface ProblemStatementPageState {
  response?: ProblemSetProblemWorksheet;
}

export class ProblemStatementPage extends React.Component<ProblemStatementPageProps, ProblemStatementPageState> {
  state: ProblemStatementPageState = {};

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

  async componentDidUpdate(prevProps: ProblemStatementPageProps, prevState: ProblemStatementPageState) {
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
      return (
        <ProblemSetProblemProgrammingStatementPage worksheet={response as ProblemSetProgrammingProblemWorksheet} />
      );
    } else {
      return <ProblemSetProblemBundleStatementPage worksheet={response as ProblemSetBundleProblemWorksheet} />;
    }
  }
}

const mapStateToProps = (state: AppState) => ({
  problemSet: selectProblemSet(state),
  problem: selectProblemSetProblem(state),
  statementLanguage: selectStatementLanguage(state),
});
const mapDispatchToProps = {
  onGetProblemWorksheet: problemSetProblemActions.getProblemWorksheet,
};
export default connect(mapStateToProps, mapDispatchToProps)(ProblemStatementPage);
