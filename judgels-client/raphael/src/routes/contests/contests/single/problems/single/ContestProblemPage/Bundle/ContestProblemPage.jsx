import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { selectContest } from '../../../../../modules/contestSelectors';
import * as breadcrumbsActions from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as contestProblemActions from '../../../modules/contestProblemActions';
import * as contestSubmissionActions from '../../../../submissions/Bundle/modules/contestSubmissionActions';

export class ContestProblemPage extends Component {
  state = {
    defaultLanguage: undefined,
    languages: undefined,
    problem: undefined,
    latestSubmissions: undefined,
    worksheet: undefined,
  };

  async componentDidMount() {
    const { defaultLanguage, languages, problem, worksheet } = await this.props.onGetProblemWorksheet(
      this.props.contest.jid,
      this.props.match.params.problemAlias,
      this.props.statementLanguage
    );

    const latestSubmissions = await this.props.onGetLatestSubmissions(this.props.contest.jid, problem.alias);
    this.setState({
      latestSubmissions,
      defaultLanguage,
      languages,
      problem,
      worksheet,
    });
    this.props.onPushBreadcrumb(this.props.match.url, 'Problem ' + problem.alias);
  }

  async componentDidUpdate(prevProps, prevState) {
    if (this.props.statementLanguage !== prevProps.statementLanguage && prevState.worksheet) {
      this.setState({ worksheet: undefined });
    } else if (!this.state.worksheet && prevState.worksheet) {
      await this.componentDidMount();
    }
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

  onCreateSubmission = async (itemJid, answer) => {
    const problem = this.state.problem;
    return await this.props.onCreateSubmission(this.props.contest.jid, problem.problemJid, itemJid, answer);
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
      <div className="language-widget-wrapper">
        <StatementLanguageWidget {...props} />
      </div>
    );
  };

  renderStatement = () => {
    const { problem, worksheet, latestSubmissions } = this.state;
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    if (!latestSubmissions) {
      return <LoadingState />;
    }
    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        latestSubmissions={latestSubmissions}
        onAnswerItem={this.onCreateSubmission}
        worksheet={worksheet}
      />
    );
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
  statementLanguage: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onGetProblemWorksheet: contestProblemActions.getBundleProblemWorksheet,
  onCreateSubmission: contestSubmissionActions.createItemSubmission,
  onGetLatestSubmissions: contestSubmissionActions.getLatestSubmissions,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ContestProblemPage));
