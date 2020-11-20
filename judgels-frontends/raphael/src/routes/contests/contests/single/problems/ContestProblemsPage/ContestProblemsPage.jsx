import * as React from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import StatementLanguageWidget from '../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { ContestProblemStatus } from '../../../../../../modules/api/uriel/contestProblem';
import { selectStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsSelectors';
import { consolidateLanguages } from '../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { ContestProblemEditDialog } from '../ContestProblemEditDialog/ContestProblemEditDialog';
import { ContestProblemCard } from '../ContestProblemCard/ContestProblemCard';
import { selectContest } from '../../../modules/contestSelectors';
import * as contestProblemActions from '../modules/contestProblemActions';

import './ContestProblemsPage.css';

export class ContestProblemsPage extends React.Component {
  state = {
    response: undefined,
    defaultLanguage: undefined,
    uniqueLanguages: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetProblems(this.props.contest.jid);
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
        <div className="contest-problems-page__header">
          {this.renderSetDialog()}
          {this.renderStatementLanguageWidget()}
          <div className="clearfix" />
        </div>
        {this.renderProblems()}
      </ContentCard>
    );
  }

  renderSetDialog = () => {
    const { response } = this.state;
    if (!response || !response.config.canManage) {
      return null;
    }

    const problems = response.data.map(p => ({
      alias: p.alias,
      slug: response.problemsMap[p.problemJid].slug,
      status: p.status,
      submissionsLimit: p.submissionsLimit,
      points: p.points,
    }));
    return (
      <ContestProblemEditDialog contest={this.props.contest} problems={problems} onSetProblems={this.setProblems} />
    );
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
      return <LoadingState />;
    }

    const { data: problems, totalSubmissionsMap } = response;

    if (problems.length === 0) {
      return (
        <p>
          <small>No problems.</small>
        </p>
      );
    }

    return (
      <div>
        {this.renderOpenProblems(
          problems.filter(p => p.status === ContestProblemStatus.Open),
          totalSubmissionsMap
        )}
        {this.renderClosedProblems(
          problems.filter(p => p.status === ContestProblemStatus.Closed),
          totalSubmissionsMap
        )}
      </div>
    );
  };

  renderOpenProblems = (problems, totalSubmissionsMap) => {
    return <div>{this.renderFilteredProblems(problems, totalSubmissionsMap)}</div>;
  };

  renderClosedProblems = (problems, totalSubmissionsMap) => {
    return (
      <div>
        {problems.length !== 0 && <hr />}
        {this.renderFilteredProblems(problems, totalSubmissionsMap)}
      </div>
    );
  };

  renderFilteredProblems = (problems, totalSubmissionsMap) => {
    return problems.map(problem => {
      const props = {
        contest: this.props.contest,
        problem,
        problemName: getProblemName(this.state.response.problemsMap[problem.problemJid], this.state.defaultLanguage),
        totalSubmissions: totalSubmissionsMap[problem.problemJid],
      };
      return <ContestProblemCard key={problem.problemJid} {...props} />;
    });
  };

  setProblems = async (contestJid, data) => {
    const response = await this.props.onSetProblems(contestJid, data);
    await this.componentDidMount();
    return response;
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
  statementLanguage: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onGetProblems: contestProblemActions.getProblems,
  onSetProblems: contestProblemActions.setProblems,
};

export default withBreadcrumb('Problems')(connect(mapStateToProps, mapDispatchToProps)(ContestProblemsPage));
