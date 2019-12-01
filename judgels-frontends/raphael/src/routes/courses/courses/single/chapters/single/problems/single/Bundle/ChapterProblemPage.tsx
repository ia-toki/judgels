import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from '../../../../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { AppState } from '../../../../../../../../../modules/store';
import { CourseChapter } from '../../../../../../../../../modules/api/jerahmeel/courseChapter';
import { ChapterProblemWorksheet } from '../../../../../../../../../modules/api/jerahmeel/chapterProblemBundle';
import { ProblemWorksheetCard } from '../../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { ItemSubmission } from '../../../../../../../../../modules/api/sandalphon/submissionBundle';
import { selectCourseChapter } from '../../../../modules/courseChapterSelectors';
import { chapterSubmissionActions as injectedChapterSubmissionActions } from '../../../results/modules/chapterSubmissionActions';

export interface ChapterProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  chapter: CourseChapter;
  worksheet: ChapterProblemWorksheet;
  onCreateSubmission: (chapterJid: string, problemJid: string, itemJid: string, answer: string) => Promise<void>;
  onGetLatestSubmissions: (chapterJid: string, problemAlias: string) => Promise<{ [id: string]: ItemSubmission }>;
}

interface ChapterProblemPageState {
  latestSubmissions?: { [id: string]: ItemSubmission };
}

export class ChapterProblemPage extends React.Component<ChapterProblemPageProps, ChapterProblemPageState> {
  state: ChapterProblemPageState = {};

  async componentDidMount() {
    const latestSubmissions = await this.props.onGetLatestSubmissions(
      this.props.chapter.chapterJid,
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

  private renderStatementLanguageWidget = () => {
    const { defaultLanguage, languages } = this.props.worksheet;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const props: StatementLanguageWidgetProps = {
      defaultLanguage: defaultLanguage,
      statementLanguages: languages,
    };
    return (
      <div className="statement-language-widget-wrapper">
        <StatementLanguageWidget {...props} />
      </div>
    );
  };

  private renderStatement = () => {
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

  private createSubmission = async (itemJid: string, answer: string) => {
    const { problem } = this.props.worksheet;
    return await this.props.onCreateSubmission(this.props.chapter.chapterJid, problem.problemJid, itemJid, answer);
  };
}

export function createChapterProblemPage(chapterSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    chapter: selectCourseChapter(state),
  });
  const mapDispatchToProps = {
    onCreateSubmission: chapterSubmissionActions.createItemSubmission,
    onGetLatestSubmissions: chapterSubmissionActions.getLatestSubmissions,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemPage));
}

export default createChapterProblemPage(injectedChapterSubmissionActions);
