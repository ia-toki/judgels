import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import { LessonStatementCard } from '../../../../../../../../../components/LessonStatementCard/LessonStatementCard';
import { AppState } from '../../../../../../../../../modules/store';
import { CourseChapter } from '../../../../../../../../../modules/api/jerahmeel/courseChapter';
import { ChapterLesson, ChapterLessonStatement } from '../../../../../../../../../modules/api/jerahmeel/chapterLesson';
import { LessonStatement } from '../../../../../../../../../modules/api/sandalphon/lesson';
import { chapterLessonActions as injectedChapterLessonActions } from '../../modules/chapterLessonActions';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import { selectCourseChapter } from '../../../../modules/courseChapterSelectors';

export interface ChapterLessonPageProps extends RouteComponentProps<{ lessonAlias: string }> {
  chapter: CourseChapter;
  onGetLessonStatement: (chapterJid: string, lessonAlias: string) => Promise<ChapterLessonStatement>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

interface ChapterLessonPageState {
  lesson?: ChapterLesson;
  statement?: LessonStatement;
}

export class ChapterLessonPage extends React.Component<ChapterLessonPageProps, ChapterLessonPageState> {
  state: ChapterLessonPageState = {};

  async componentDidMount() {
    const { lesson, statement } = await this.props.onGetLessonStatement(
      this.props.chapter.chapterJid,
      this.props.match.params.lessonAlias
    );

    this.setState({
      lesson,
      statement,
    });

    this.props.onPushBreadcrumb(this.props.match.url, lesson.alias + '. ' + statement.title);
  }

  async componentDidUpdate(prevProps: ChapterLessonPageProps, prevState: ChapterLessonPageState) {
    if (!this.state.statement && prevState.statement) {
      await this.componentDidMount();
    }
  }

  async componentWillUnmount() {
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    return <ContentCard>{this.renderStatement()}</ContentCard>;
  }

  private renderStatement = () => {
    const { lesson, statement } = this.state;
    if (!lesson || !statement) {
      return <LoadingState />;
    }

    return <LessonStatementCard alias={lesson.alias} statement={statement} />;
  };
}

export function createChapterLessonPage(chapterLessonActions, breadcrumbsActions) {
  const mapStateToProps = (state: AppState) => ({
    chapter: selectCourseChapter(state).courseChapter,
  });

  const mapDispatchToProps = {
    onGetLessonStatement: chapterLessonActions.getLessonStatement,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ChapterLessonPage));
}

export default createChapterLessonPage(injectedChapterLessonActions, injectedBreadcrumbsActions);
