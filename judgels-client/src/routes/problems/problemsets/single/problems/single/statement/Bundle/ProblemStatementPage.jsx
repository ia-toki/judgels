import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';

import * as problemSetSubmissionActions from '../../results/modules/problemSetSubmissionActions';

export class ProblemStatementPage extends Component {
  state = {
    latestSubmissions: undefined,
  };

  async componentDidMount() {
    const latestSubmissions = await this.props.onGetLatestSubmissions(
      this.props.problemSet.jid,
      this.props.worksheet.problem.alias
    );
    this.setState({
      latestSubmissions,
    });
  }

  render() {
    return (
      <ContentCard>
        {this.renderStatementLanguageWidget()}
        {this.renderStatement()}
      </ContentCard>
    );
  }

  renderStatementLanguageWidget = () => {
    const { defaultLanguage, languages } = this.props.worksheet;
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
    const { problem, worksheet } = this.props.worksheet;
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    const { latestSubmissions } = this.state;
    if (!latestSubmissions) {
      return <LoadingState />;
    }
    const resultsUrl = (this.props.location.pathname + '/results').replace('//', '/');

    return (
      <ProblemWorksheetCard
        latestSubmissions={latestSubmissions}
        onAnswerItem={this.createSubmission}
        worksheet={worksheet}
        resultsUrl={resultsUrl}
      />
    );
  };

  createSubmission = async (itemJid, answer) => {
    const { problem } = this.props.worksheet;
    return await this.props.onCreateSubmission(this.props.problemSet.jid, problem.problemJid, itemJid, answer);
  };
}

const mapStateToProps = state => ({
  problemSet: selectProblemSet(state),
});
const mapDispatchToProps = {
  onCreateSubmission: problemSetSubmissionActions.createItemSubmission,
  onGetLatestSubmissions: problemSetSubmissionActions.getLatestSubmissions,
};
export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ProblemStatementPage));
