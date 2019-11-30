import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { AppState } from '../../../../../../../../modules/store';
import { ProblemType } from '../../../../../../../../modules/api/sandalphon/problem';
import { ProblemSet } from '../../../../../../../../modules/api/jerahmeel/problemSet';
import {
  ProblemSetProblemWorksheet,
  ProblemSetProblem,
} from '../../../../../../../../modules/api/jerahmeel/problemSetProblem';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import ProblemSetProblemProgrammingStatementPage from '../Programming/ProblemStatementPage';
import ProblemSetProblemBundleStatementPage from '../Bundle/ProblemStatementPage';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { problemSetProblemActions as injectedProblemSetProblemActions } from '../../../modules/problemSetProblemActions';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';

export interface ProblemStatementPageProps extends RouteComponentProps {
  problemSet: ProblemSet;
  problemSetProblem: ProblemSetProblem;
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
      this.props.problemSetProblem.alias,
      this.props.statementLanguage
    );

    this.setState({
      response,
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
      return <ProblemSetProblemProgrammingStatementPage worksheet={response} />;
    } else {
      return <ProblemSetProblemBundleStatementPage worksheet={response} />;
    }
  }
}

export function createProblemStatementPage(problemSetProblemActions) {
  const mapStateToProps = (state: AppState) => ({
    problemSet: selectProblemSet(state),
    problemSetProblem: selectProblemSetProblem(state),
    statementLanguage: selectStatementLanguage(state),
  });
  const mapDispatchToProps = {
    onGetProblemWorksheet: problemSetProblemActions.getProblemWorksheet,
  };
  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ProblemStatementPage));
}

export default createProblemStatementPage(injectedProblemSetProblemActions);
