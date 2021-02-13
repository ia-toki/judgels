import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../../../components/LoadingContentCard/LoadingContentCard';
import StatementLanguageWidget from '../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { ProblemSetProblemCard } from '../ProblemSetProblemCard/ProblemSetProblemCard';
import { consolidateLanguages } from '../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { selectProblemSet } from '../../../modules/problemSetSelectors';
import { selectStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsSelectors';
import * as problemSetProblemActions from '../modules/problemSetProblemActions';

export class ProblemSetProblemsPage extends Component {
  state = {
    response: undefined,
    defaultLanguage: undefined,
    uniqueLanguages: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetProblems(this.props.problemSet.jid);
    const { defaultLanguage, uniqueLanguages } = consolidateLanguages(
      response.problemsMap,
      this.props.statementLanguage
    );

    this.setState({
      response,
      defaultLanguage,
      uniqueLanguages,
    });
  }

  async componentDidUpdate(prevProps) {
    const { response } = this.state;
    if (this.props.statementLanguage !== prevProps.statementLanguage && response) {
      const { defaultLanguage, uniqueLanguages } = consolidateLanguages(
        response.problemsMap,
        this.props.statementLanguage
      );

      this.setState({
        defaultLanguage,
        uniqueLanguages,
      });
    }
  }

  render() {
    return (
      <ContentCard>
        <h3>Problems</h3>
        <hr />
        {this.renderStatementLanguageWidget()}
        {this.renderProblems()}
      </ContentCard>
    );
  }

  renderStatementLanguageWidget = () => {
    const { defaultLanguage, uniqueLanguages } = this.state;
    if (!defaultLanguage || !uniqueLanguages) {
      return null;
    }

    const props = {
      defaultLanguage,
      statementLanguages: uniqueLanguages,
    };
    return <StatementLanguageWidget {...props} />;
  };

  renderProblems = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: problems, problemProgressesMap, problemStatsMap } = response;

    if (problems.length === 0) {
      return (
        <p>
          <small>No problems.</small>
        </p>
      );
    }

    return problems.map(problem => {
      const problemInfo = this.state.response.problemsMap[problem.problemJid] || {};
      const props = {
        problemSet: this.props.problemSet,
        problem,
        problemName: getProblemName(this.state.response.problemsMap[problem.problemJid], this.state.defaultLanguage),
        hasEditorial: problemInfo.hasEditorial,
        progress: problemProgressesMap[problem.problemJid],
        stats: problemStatsMap[problem.problemJid],
      };
      return <ProblemSetProblemCard key={problem.problemJid} {...props} />;
    });
  };
}

const mapStateToProps = state => ({
  problemSet: selectProblemSet(state),
  statementLanguage: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onGetProblems: problemSetProblemActions.getProblems,
};

export default connect(mapStateToProps, mapDispatchToProps)(ProblemSetProblemsPage);
