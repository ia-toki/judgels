import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../../../../../components/LoadingContentCard/LoadingContentCard';
import StatementLanguageWidget, {
  StatementLanguageWidgetProps,
} from '../../../../../../../../components/StatementLanguageWidget/StatementLanguageWidget';
import { ChapterProblemCard, ChapterProblemCardProps } from '../ChapterProblemCard/ChapterProblemCard';
import { consolidateLanguages } from '../../../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../../../modules/api/sandalphon/problem';
import { Course } from '../../../../../../../../modules/api/jerahmeel/course';
import { CourseChapter } from '../../../../../../../../modules/api/jerahmeel/courseChapter';
import { ChapterProblemsResponse } from '../../../../../../../../modules/api/jerahmeel/chapterProblem';
import { AppState } from '../../../../../../../../modules/store';
import { selectCourse } from '../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../modules/courseChapterSelectors';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { chapterProblemActions as injectedChapterProblemActions } from '../modules/chapterProblemActions';

export interface ChapterProblemsPageProps {
  course: Course;
  chapter: CourseChapter;
  statementLanguage: string;
  onGetProblems: (chapterJid: string) => Promise<ChapterProblemsResponse>;
}

interface ChapterProblemsPageState {
  response?: ChapterProblemsResponse;
  defaultLanguage?: string;
  uniqueLanguages?: string[];
}

export class ChapterProblemsPage extends React.PureComponent<ChapterProblemsPageProps, ChapterProblemsPageState> {
  state: ChapterProblemsPageState = {};

  async componentDidMount() {
    const response = await this.props.onGetProblems(this.props.chapter.chapterJid);
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

  async componentDidUpdate(prevProps: ChapterProblemsPageProps) {
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

  private renderStatementLanguageWidget = () => {
    const { defaultLanguage, uniqueLanguages } = this.state;
    if (!defaultLanguage || !uniqueLanguages) {
      return null;
    }

    const props: StatementLanguageWidgetProps = {
      defaultLanguage,
      statementLanguages: uniqueLanguages,
    };
    return <StatementLanguageWidget {...props} />;
  };

  private renderProblems = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: problems, problemsMap, problemProgressesMap } = response;

    if (problems.length === 0) {
      return (
        <p>
          <small>No problems.</small>
        </p>
      );
    }

    return problems.map(problem => {
      const props: ChapterProblemCardProps = {
        course: this.props.course,
        chapter: this.props.chapter,
        problem,
        problemName: getProblemName(problemsMap[problem.problemJid], this.state.defaultLanguage),
        progress: problemProgressesMap[problem.problemJid],
      };
      return <ChapterProblemCard key={problem.problemJid} {...props} />;
    });
  };
}

export function createChapterProblemsPage(chapterProblemActions) {
  const mapStateToProps = (state: AppState) => ({
    course: selectCourse(state),
    chapter: selectCourseChapter(state),
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetProblems: chapterProblemActions.getProblems,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemsPage));
}

export default createChapterProblemsPage(injectedChapterProblemActions);
