import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectGradingLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import { selectContest } from '../../../../../modules/contestSelectors';
import * as contestProblemActions from '../../../modules/contestProblemActions';
import * as contestSubmissionActions from '../../../../submissions/Programming/modules/contestSubmissionActions';
import * as webPrefsActions from '../../../../../../../../modules/webPrefs/webPrefsActions';
import * as breadcrumbsActions from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';

import './ContestProblemPage.css';

export class ContestProblemPage extends React.Component {
  state = {
    defaultLanguage: undefined,
    languages: undefined,
    problem: undefined,
    totalSubmissions: undefined,
    worksheet: undefined,
  };

  async componentDidMount() {
    await this.fetchProblemWorksheet();
    this.props.onPushBreadcrumb(this.props.match.url, 'Problem ' + this.state.problem.alias);
  }

  async componentDidUpdate(prevProps, prevState) {
    if (this.props.statementLanguage !== prevProps.statementLanguage && prevState.worksheet) {
      this.setState({ worksheet: undefined });
    } else if (!this.state.worksheet && prevState.worksheet) {
      await this.fetchProblemWorksheet();
    }
  }

  async fetchProblemWorksheet() {
    const { defaultLanguage, languages, problem, totalSubmissions, worksheet } = await this.props.onGetProblemWorksheet(
      this.props.contest.jid,
      this.props.match.params.problemAlias,
      this.props.statementLanguage
    );

    this.setState({
      defaultLanguage,
      languages,
      problem,
      totalSubmissions,
      worksheet,
    });
  }

  async componentWillUnmount() {
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    return (
      <ContentCard>
        {this.renderStatementLanguageWidget()}
        {this.renderStatement()}
      </ContentCard>
    );
  }

  createSubmission = async data => {
    const problem = this.state.problem;
    this.props.onUpdateGradingLanguage(data.gradingLanguage);
    return await this.props.onCreateSubmission(
      this.props.contest.jid,
      this.props.contest.slug,
      problem.problemJid,
      data
    );
  };

  renderStatementLanguageWidget = () => {
    const { defaultLanguage, languages } = this.state;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const props = {
      defaultLanguage: defaultLanguage,
      statementLanguages: languages,
    };
    return (
      <div className="contest-programming-problem-page__widget">
        <StatementLanguageWidget {...props} />
      </div>
    );
  };

  renderStatement = () => {
    const { problem, totalSubmissions, worksheet } = this.state;
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    let submissionWarning;
    if (!!problem.submissionsLimit) {
      const submissionsLeft = problem.submissionsLimit - totalSubmissions;
      submissionWarning = '' + submissionsLeft + ' submissions left.';
    }

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        worksheet={worksheet}
        onSubmit={this.createSubmission}
        submissionWarning={submissionWarning}
        gradingLanguage={this.props.gradingLanguage}
      />
    );
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
  statementLanguage: selectStatementLanguage(state),
  gradingLanguage: selectGradingLanguage(state),
});

const mapDispatchToProps = {
  onGetProblemWorksheet: contestProblemActions.getProgrammingProblemWorksheet,
  onCreateSubmission: contestSubmissionActions.createSubmission,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  onUpdateGradingLanguage: webPrefsActions.updateGradingLanguage,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ContestProblemPage));
