import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingContentCard } from '../../../../../../components/LoadingContentCard/LoadingContentCard';
import { ProblemSetProblemCard } from '../../../../../../components/ProblemSetProblemCard/ProblemSetProblemCard';
import ProblemSpoilerWidget from '../../../../../../components/ProblemSpoilerWidget/ProblemSpoilerWidget';
import { consolidateLanguages } from '../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { selectStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectProblemSet } from '../../../modules/problemSetSelectors';

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
        {this.renderHeader()}
        {this.renderProblems()}
      </ContentCard>
    );
  }

  renderHeader = () => {
    return (
      <>
        <div className="float-left">{this.renderProblemSpoilerWidget()}</div>
        <div className="float-right">{this.renderStatementLanguageWidget()}</div>
        <div className="clearfix" />
      </>
    );
  };

  renderProblemSpoilerWidget = () => {
    return <ProblemSpoilerWidget />;
  };

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

    const { data: problems, problemsMap, problemMetadatasMap, problemDifficultiesMap, problemProgressesMap } = response;

    if (problems.length === 0) {
      return (
        <p>
          <small>No problems.</small>
        </p>
      );
    }

    return problems.map(problem => {
      const props = {
        problemSet: this.props.problemSet,
        problem,
        showAlias: true,
        problemName: getProblemName(problemsMap[problem.problemJid], this.state.defaultLanguage),
        metadata: problemMetadatasMap[problem.problemJid],
        difficulty: problemDifficultiesMap[problem.problemJid],
        progress: problemProgressesMap[problem.problemJid],
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
