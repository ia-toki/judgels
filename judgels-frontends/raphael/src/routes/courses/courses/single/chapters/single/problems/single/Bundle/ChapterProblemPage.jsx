import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { ProblemWorksheetCard } from '../../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { selectCourseChapter } from '../../../../modules/courseChapterSelectors';
import * as chapterSubmissionActions from '../../../results/modules/chapterSubmissionActions';

export class ChapterProblemPage extends Component {
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

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        latestSubmissions={latestSubmissions}
        onAnswerItem={this.createSubmission}
        worksheet={worksheet}
      />
    );
  };

  createSubmission = async (itemJid, answer) => {
    const { problem } = this.props.worksheet;
    return await this.props.onCreateSubmission(this.props.chapter.jid, problem.problemJid, itemJid, answer);
  };
}

const mapStateToProps = state => ({
  chapter: selectCourseChapter(state),
});
const mapDispatchToProps = {
  onCreateSubmission: chapterSubmissionActions.createItemSubmission,
  onGetLatestSubmissions: chapterSubmissionActions.getLatestSubmissions,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemPage));
