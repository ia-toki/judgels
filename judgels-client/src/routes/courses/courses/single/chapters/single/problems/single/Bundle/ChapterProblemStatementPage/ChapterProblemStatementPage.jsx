import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { selectCourseChapter } from '../../../../../modules/courseChapterSelectors';

import * as chapterProblemSubmissionActions from '../submissions/modules/chapterProblemSubmissionActions';

import './ChapterProblemStatementPage.scss';

export class ChapterProblemStatementPage extends Component {
  state = {
    latestSubmissions: undefined,
  };

  async componentDidMount() {
    const latestSubmissions = await this.props.onGetLatestSubmissions(
      this.props.chapter.jid,
      this.props.worksheet.problem.alias
    );
    this.setState({
      latestSubmissions,
    });
  }

  render() {
    return (
      <ContentCard className="chapter-bundle-problem-statement-page">
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

    const reasonNotAllowedToSubmit = this.isInSubmissionsPath()
      ? 'Submission received.'
      : worksheet.reasonNotAllowedToSubmit;

    const resultsUrl = (this.props.location.pathname + '/submissions').replace('//', '/');

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        latestSubmissions={latestSubmissions}
        onAnswerItem={this.createSubmission}
        worksheet={{ ...worksheet, reasonNotAllowedToSubmit }}
        resultsUrl={resultsUrl}
      />
    );
  };

  createSubmission = async (itemJid, answer) => {
    const { problem } = this.props.worksheet;
    return await this.props.onCreateSubmission(this.props.chapter.jid, problem.problemJid, itemJid, answer);
  };

  isInSubmissionsPath = () => {
    return (this.props.location.pathname + '/').includes('/submissions/');
  };
}

const mapStateToProps = state => ({
  chapter: selectCourseChapter(state),
});
const mapDispatchToProps = {
  onCreateSubmission: chapterProblemSubmissionActions.createItemSubmission,
  onGetLatestSubmissions: chapterProblemSubmissionActions.getLatestSubmissions,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemStatementPage));
