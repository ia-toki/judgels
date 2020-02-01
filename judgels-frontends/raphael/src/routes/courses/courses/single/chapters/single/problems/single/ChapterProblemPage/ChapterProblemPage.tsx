import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { sendGAEvent } from '../../../../../../../../../ga';
import { AppState } from '../../../../../../../../../modules/store';
import { ProblemType } from '../../../../../../../../../modules/api/sandalphon/problem';
import { Course } from '../../../../../../../../../modules/api/jerahmeel/course';
import { CourseChapter } from '../../../../../../../../../modules/api/jerahmeel/courseChapter';
import { ChapterProblemWorksheet } from '../../../../../../../../../modules/api/jerahmeel/chapterProblem';
import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import ChapterProblemProgrammingPage from '../Programming/ChapterProblemPage';
import ChapterProblemBundlePage from '../Bundle/ChapterProblemPage';
import { selectCourse } from '../../../../../../modules/courseSelectors';
import { selectCourseChapter, selectCourseChapterName } from '../../../../modules/courseChapterSelectors';
import { selectStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { chapterProblemActions as injectedChapterProblemActions } from '../../modules/chapterProblemActions';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';

export interface ChapterProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  course: Course;
  chapter: CourseChapter;
  chapterName: string;
  statementLanguage: string;
  gradingLanguage: string;
  onGetProblemWorksheet: (
    chapterJid: string,
    problemAlias: string,
    language?: string
  ) => Promise<ChapterProblemWorksheet>;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

interface ChapterProblemPageState {
  response?: ChapterProblemWorksheet;
}

export class ChapterProblemPage extends React.Component<ChapterProblemPageProps, ChapterProblemPageState> {
  state: ChapterProblemPageState = {};

  async componentDidMount() {
    const response = await this.props.onGetProblemWorksheet(
      this.props.chapter.chapterJid,
      this.props.match.params.problemAlias,
      this.props.statementLanguage
    );

    this.setState({
      response,
    });

    this.props.onPushBreadcrumb(this.props.match.url, response.problem.alias);

    sendGAEvent({ category: 'Courses', action: 'View course problem', label: this.props.course.name });
    sendGAEvent({ category: 'Courses', action: 'View chapter problem', label: this.props.chapterName });
    sendGAEvent({
      category: 'Courses',
      action: 'View problem',
      label: this.props.chapterName + ': ' + this.props.match.params.problemAlias,
    });
  }

  async componentDidUpdate(prevProps: ChapterProblemPageProps, prevState: ChapterProblemPageState) {
    if (this.props.statementLanguage !== prevProps.statementLanguage && prevState.response) {
      this.setState({ response: undefined });
    } else if (!this.state.response && prevState.response) {
      await this.componentDidMount();
    }
  }

  async componentWillUnmount() {
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }
    const { problem } = response;
    if (problem.type === ProblemType.Programming) {
      return <ChapterProblemProgrammingPage worksheet={response} />;
    } else {
      return <ChapterProblemBundlePage worksheet={response} />;
    }
  }
}

export function createChapterProblemPage(chapterProblemActions, breadcrumbsActions) {
  const mapStateToProps = (state: AppState) => ({
    course: selectCourse(state),
    chapter: selectCourseChapter(state),
    chapterName: selectCourseChapterName(state),
    statementLanguage: selectStatementLanguage(state),
  });
  const mapDispatchToProps = {
    onGetProblemWorksheet: chapterProblemActions.getProblemWorksheet,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };
  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemPage));
}

export default createChapterProblemPage(injectedChapterProblemActions, injectedBreadcrumbsActions);
